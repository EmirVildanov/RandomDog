package com.vildanov.randomdog.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vildanov.randomdog.network.DogImageProperty
import com.vildanov.randomdog.network.RandomDogApi
import kotlinx.coroutines.*
import timber.log.Timber

class HomeViewModel : ViewModel() {

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _currentDogPictureData = MutableLiveData<DogImageProperty>()
    val currentDogPictureData: LiveData<DogImageProperty>
        get() = _currentDogPictureData

    fun getNewImageUrl() {
        coroutineScope.launch {
            try {
                _currentDogPictureData.value = RandomDogApi.retrofitService.getProperties()
            } catch (e: Exception) {
                Timber.i("Error while loading image")
            }
        }
    }
}