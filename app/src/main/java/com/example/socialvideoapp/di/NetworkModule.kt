package com.example.socialvideoapp.di

import com.example.socialvideoapp.network.VideosApi
import com.example.socialvideoapp.utils.Constants
import com.example.socialvideoapp.utils.Constants.API_KEY
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    // Provides a Retrofit instance
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val client: OkHttpClient = Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", API_KEY)
                .build()
            chain.proceed(newRequest)
        }.build()

        // Create a Retrofit instance
        return Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides a VideosApi instance
    @Singleton
    @Provides
    fun provideVideosApi(retrofit: Retrofit): VideosApi {
        // Create and Return the VideosApi instance
        return retrofit.create(VideosApi::class.java)
    }
}