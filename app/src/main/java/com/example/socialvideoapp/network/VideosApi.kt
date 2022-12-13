package com.example.socialvideoapp.network

import com.example.socialvideoapp.data.models.VideosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideosApi {

    // Gets the list of videos
    @GET("videos/popular")
    suspend fun getVideos(@Query("per_page") perPage: Int): Response<VideosResponse>
}