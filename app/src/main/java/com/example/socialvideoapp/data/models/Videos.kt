package com.example.socialvideoapp.data.models

data class Videos(
    val id: String,
    val image: String?,
    val video_files: List<VideoFiles>?,
)

data class VideoFiles(
    val id: String,
    val link: String?
)
