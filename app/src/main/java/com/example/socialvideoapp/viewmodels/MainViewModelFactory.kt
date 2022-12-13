package com.example.socialvideoapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialvideoapp.data.repository.VideoRepository
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val repository: VideoRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}