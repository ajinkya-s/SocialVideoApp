package com.example.socialvideoapp

import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.example.socialvideoapp.data.localData.Video
import com.example.socialvideoapp.data.localData.VideoDao
import com.example.socialvideoapp.data.models.VideoFiles
import com.example.socialvideoapp.data.models.Videos
import com.example.socialvideoapp.data.models.VideosResponse
import com.example.socialvideoapp.data.repository.VideoRepository
import com.example.socialvideoapp.network.VideosApi
import com.example.socialvideoapp.utils.Constants.SORT_PREFERENCE
import com.example.socialvideoapp.utils.VideoUtils
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Response
import java.lang.System.currentTimeMillis


class VideoRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<SocialVideoApplication>()
    private val preferences = mock(SharedPreferences::class.java)
    private val videosApi = mock(VideosApi::class.java)
    private val videoDao = mock(VideoDao::class.java)

    private val repository = VideoRepository(videosApi, context, preferences)

    @Test
    fun testFetchVideos() = runBlocking {
        // Set up the mock responses.
        val body = VideosResponse(listOf(Videos(id = "1", image = "Foo", video_files = listOf(
            VideoFiles(id="1", link="abc") ))))
        val response = Response.success(body)
        `when`(videosApi.getVideos(20)).thenReturn(response)

        // Call the method being tested.
        repository.fetchVideos()

        // Verify that the video DAO's insertAll() method was called with the expected parameters.
        verify(videoDao).insertAll(VideoUtils.mapRemoteResponseToLocal(body.videos))
    }

    @Test
    fun testGetVideos() = runBlocking {
        // Set up the mock responses.
        val videos = listOf(Video(id = "1", url = "Foo", image = "xyz", viewCount = 2))
        `when`(videoDao.getVideosSortedByMostViewed()).thenReturn(videos)

        // Call the method being tested.
        val result = repository.getVideos()

        // Verify that the result is as expected.
        assertEquals(videos, result)
    }

    @Test
    fun testGetRecentVideos() = runBlocking {
        // Set up the mock responses.
        val videos = listOf(Video(id = "1", url = "Foo", image = "xyz", lastViewedTime = 2222))
        `when`(videoDao.getVideosSortedByRecentViewed()).thenReturn(videos)

        // Call the method being tested.
        val result = repository.getRecentVideos()

        // Verify that the result is as expected.
        assertEquals(videos, result)
    }

    @Test
    fun testUpdateViewCount() = runBlocking {
        // Set up the video to be updated.
        val video = Video(id = "1", url = "Foo", viewCount = 10, image = "xyz")

        // Call the method being tested.
        repository.updateViewCount(video)

        // Verify that the video DAO's update() method was called with the expected parameters.
        verify(videoDao).update(video.copy(viewCount = 11))
    }

    @Test
    fun testUpdateLastViewedTime() = runBlocking {
        // Set up the video to be updated.
        val video = Video(id = "1", url = "Foo", lastViewedTime = 0, image = "xyz")

        // Call the method being tested.
        repository.updateLastViewedTime(video)

        // Verify that the video DAO's update() method was called with the expected parameters.
        verify(videoDao).update(video.copy(lastViewedTime = currentTimeMillis()))
    }

    @Test
    fun testGetVideoSortPreference() {
        // Set up the mock responses.
        `when`(preferences.getInt(SORT_PREFERENCE, 0)).thenReturn(1)

        // Call the method being tested.
        val result = repository.getVideoSortPreference()

        // Verify that the result is as expected.
        assertEquals(1, result)
    }

    @Test
    fun testSaveVideoSortPreference() {
        // Call the method being tested.
        repository.saveVideoSortPreference(1)

        // Verify that the preferences object's edit() method was called with the expected parameters.
        verify(preferences).edit().putInt(SORT_PREFERENCE, 1)
    }
}

