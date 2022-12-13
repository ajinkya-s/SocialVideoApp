package com.example.socialvideoapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialvideoapp.data.localData.Video
import com.example.socialvideoapp.data.repository.VideoRepository
import com.example.socialvideoapp.utils.SortOptions.MOST_VIEWED
import com.example.socialvideoapp.utils.SortOptions.RECENTLY_VIEWED
import kotlinx.coroutines.launch

class MainViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    // LiveData for the video list
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos

    // LiveData for the selected video
    val _selectedVideo = MutableLiveData<Video>()
    val selectedVideo: LiveData<Video> = _selectedVideo

    init {
        viewModelScope.launch {
            val sortBy = videoRepository.getVideoSortPreference()
            val videosList = videoRepository.getVideos()
            if (videosList.isNullOrEmpty()) {
                videoRepository.fetchVideos()
            }

            _videos.value = when (sortBy) {
                RECENTLY_VIEWED -> videoRepository.getRecentVideos()
                MOST_VIEWED -> videoRepository.getVideos() ?: listOf()
                else -> {
                    listOf<Video>()
                }
            }
            _selectedVideo.value = _videos.value?.get(0)
        }
    }

    fun onVideoClicked(video: Video) {
        // Update the recentlyViewed and viewCount field of the video when it is clicked
        val updatedVideo = video.copy(lastViewedTime = System.currentTimeMillis())
        viewModelScope.launch {
            videoRepository.updateLastViewedTime(updatedVideo)
            videoRepository.updateViewCount(updatedVideo)
        }

        // Sort the list of videos again to reflect the changes
        val sortBy = videoRepository.getVideoSortPreference()
        sortAndUpdateVideosList(sortBy)
    }

    fun sortVideos(sortBy: Int) {
        // Save the current sorting preference in Shared Preferences
        videoRepository.saveVideoSortPreference(sortBy)

        sortAndUpdateVideosList(sortBy)
    }

    private fun sortAndUpdateVideosList(sortBy: Int) {
        // Sort the list of videos based on the selected sorting preference
        viewModelScope.launch {
            _videos.value = when (sortBy) {
                RECENTLY_VIEWED -> videoRepository.getRecentVideos()
                MOST_VIEWED -> videoRepository.getVideos() ?: listOf()
                else -> {
                    listOf<Video>()
                }
            }
        }
    }
}