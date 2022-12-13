package com.example.socialvideoapp.di

import android.content.Context
import com.example.socialvideoapp.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainActivityModule::class, NetworkModule::class])
interface AppComponent {
    fun context(): Context?

    fun inject(mainActivity: MainActivity)
}