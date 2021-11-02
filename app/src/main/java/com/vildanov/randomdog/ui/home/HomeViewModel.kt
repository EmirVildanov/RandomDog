package com.vildanov.randomdog.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vildanov.randomdog.network.DogPictureData
import com.vildanov.randomdog.network.RandomDogApi
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

class HomeViewModel : ViewModel() {

    private var viewModelJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _currentDogPictureData = MutableLiveData<DogPictureData>()
    val currentDogPictureData: LiveData<DogPictureData>
        get() = _currentDogPictureData

    @KtorExperimentalAPI
    @ExperimentalSerializationApi
    fun getNewImageUrl() {
        coroutineScope.launch {
            val response = RandomDogApi.getNewDog()
            response?.let { _currentDogPictureData.postValue(it) }
        }
    }
}