package com.vildanov.randomdog.ui.library

import android.app.Application
import androidx.lifecycle.*
import com.vildanov.randomdog.network.DogPictureData
import com.vildanov.randomdog.database.getDatabase
import com.vildanov.randomdog.repository.DogImagesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)
    private val dogImagesRepository = DogImagesRepository(database)

    val library = dogImagesRepository.dogImages

    init {
        viewModelScope.launch {
            dogImagesRepository.refreshDogImages()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LibraryViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}