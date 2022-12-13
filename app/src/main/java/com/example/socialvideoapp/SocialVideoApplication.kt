package com.example.socialvideoapp

import android.app.Application
import com.example.socialvideoapp.di.AppComponent
import com.example.socialvideoapp.di.DaggerAppComponent
import com.example.socialvideoapp.di.MainActivityModule

class SocialVideoApplication : Application() {

    lateinit var applicationComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerAppComponent.builder()
            .mainActivityModule(MainActivityModule(applicationContext))
            .build();
    }
}