package com.example.randomdog.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.randomdog.data.DogPictureData

class LibraryViewModel : ViewModel() {

    private val _downloadedImages = MutableLiveData<List<DogPictureData>>()
    val downloadedImages: LiveData<List<DogPictureData>>
        get() = _downloadedImages
}