package com.example.socialvideoapp.utils

object Constants {
    const val BASE_URL = "https://api.pexels.com"
    const val DATABASE_NAME = "video_database"
    const val API_KEY = "563492ad6f91700001000001b5bf6a2d98324ed08ed168db1734207a"
    const val PROGRESS_PREFIX = "video_progress_"
    const val SORT_PREFERENCE = "sort_preference"
}

object SortOptions {
    const val RECENTLY_VIEWED = 0
    const val MOST_VIEWED = 1
    val SORT_OPTIONS = arrayOf<CharSequence>("Recently Viewed", "Most Viewed")
}