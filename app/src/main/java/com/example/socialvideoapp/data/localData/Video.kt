package com.example.socialvideoapp.data.localData

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define the schema for the videos table.
@Entity
data class Video(
    @PrimaryKey val id: String,
    val url: String?,
    val image: String?,
    val viewCount: Int? = 0,
    val lastViewedTime: Long? = 0
)