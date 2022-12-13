package com.example.socialvideoapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.socialvideoapp.data.localData.Video
import com.example.socialvideoapp.data.localData.VideoDao
import com.example.socialvideoapp.data.localData.VideoDatabase
import com.example.socialvideoapp.network.VideosApi
import com.example.socialvideoapp.utils.Constants.PROGRESS_PREFIX
import com.example.socialvideoapp.utils.Constants.SORT_PREFERENCE
import com.example.socialvideoapp.utils.VideoUtils
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val videosApi: VideosApi,
    private val context: Context,
    private val preferences: SharedPreferences
) {

    private val videoDao: VideoDao = VideoDatabase.invoke(context).getVideoDao()

    // Fetch videos from the API and store them in the database.
    suspend fun fetchVideos() {
        val result = videosApi.getVideos(20)
        if (result.isSuccessful && result.body() != null && result.body()?.videos != null) {
            videoDao.insertAll(VideoUtils.mapRemoteResponseToLocal(result.body()?.videos!!))
        }
    }

    // Get the list of videos from the database, sorted by most viewed.
    suspend fun getVideos(): List<Video>? {
        return videoDao.getVideosSortedByMostViewed()
    }

    // Get the list of videos from the database, sorted by recently viewed.
    suspend fun getRecentVideos(): List<Video> {
        return videoDao.getVideosSortedByRecentViewed()
    }

    // Update the view count for the given video.
    suspend fun updateViewCount(video: Video) {
        val updatedVideo = video.copy(viewCount = video.viewCount!! + 1)
        videoDao.update(updatedVideo)
    }

    // Update the last viewed time for the given video.
    suspend fun updateLastViewedTime(video: Video) {
        val updatedVideo = video.copy(lastViewedTime = video.lastViewedTime)
        videoDao.update(updatedVideo)
    }

    // Returns the sorting preference of the videos list
    fun getVideoSortPreference(): Int {
        // Get the sorting preference value from SharedPreferences
        return preferences.getInt(SORT_PREFERENCE, 0)
    }

    // Saves the sorting preference of the videos list
    fun saveVideoSortPreference(preference: Int) {
        // Save the sorting preference value in SharedPreferences
        preferences.edit {
            putInt(SORT_PREFERENCE, preference)
        }
    }
}