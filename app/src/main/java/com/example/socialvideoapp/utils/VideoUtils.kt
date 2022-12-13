package com.example.socialvideoapp.utils

import com.example.socialvideoapp.data.localData.Video
import com.example.socialvideoapp.data.models.Videos

class VideoUtils {
    companion object {
        fun mapRemoteResponseToLocal(videos: List<Videos>): List<Video> {
           return videos.map {
                Video(
                    id = it.id,
                    image = it.image,
                    url = if (!it.video_files.isNullOrEmpty()) it.video_files[0].link else "",
                )
            }
        }
    }
}