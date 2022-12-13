package com.example.socialvideoapp.data.localData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Define the DAO for the videos table.
@Dao
interface VideoDao {
    @Insert
    suspend fun insertAll(videos: List<Video>)

    @Query("SELECT * FROM video ORDER BY viewCount DESC")
    suspend fun getVideosSortedByMostViewed(): List<Video>?

    @Query("SELECT * FROM video ORDER BY lastViewedTime DESC")
    suspend fun getVideosSortedByRecentViewed(): List<Video>

    @Query("SELECT * FROM video WHERE id = :id")
    suspend fun getVideoById(id: String): Video?

    @Update
    suspend fun update(video: Video)

    @Query("DELETE FROM video")
    suspend fun deleteAll()
}

