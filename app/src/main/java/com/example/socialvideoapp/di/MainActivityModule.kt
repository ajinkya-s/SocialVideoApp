package com.example.socialvideoapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityModule(context: Context) {
    private val context: Context

    init {
        this.context = context
    }

    @Provides //scope is not necessary for parameters stored within the module
    fun context(): Context {
        return context
    }

    // Provides a SharedPreferences instance
    @Singleton
    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("MyPref", MODE_PRIVATE)
    }
}