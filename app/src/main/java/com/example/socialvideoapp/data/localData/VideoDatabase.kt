package com.example.socialvideoapp.data.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.socialvideoapp.utils.Constants.DATABASE_NAME

@Database(entities = [Video::class], version = 1, exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun getVideoDao(): VideoDao

    companion object {

        @Volatile // All threads have immediate access to this property
        private var instance: VideoDatabase? = null

        private val LOCK = Any() // Makes sure no threads making the same thing at the same time

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                VideoDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
    }
}