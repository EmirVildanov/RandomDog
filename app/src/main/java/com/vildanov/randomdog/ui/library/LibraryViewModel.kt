package com.vildanov.randomdog.ui.library

import android.app.Application
import androidx.lifecycle.*
import com.vildanov.randomdog.data.DogPictureData
import com.vildanov.randomdog.database.getDatabase
import com.vildanov.randomdog.network.RandomDogApi
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val _downloadedImages = MutableLiveData<List<DogPictureData>>()
    val downloadedImages: LiveData<List<DogPictureData>>
        get() = _downloadedImages

    private val database = getDatabase(application)

    fun refreshDataFromNetwork() = viewModelScope.launch {
        val dogImages = RandomDogApi.getNewDog()?.let {
            val previousListCopy = _downloadedImages.value
            previousListCopy!!.toMutableList().add(it)
            _downloadedImages.postValue(previousListCopy!!)
        }
    }
}