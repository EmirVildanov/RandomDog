package com.vildanov.randomdog.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.vildanov.randomdog.data.DogPictureData
import com.vildanov.randomdog.database.DogImagesDatabase
import com.vildanov.randomdog.network.NetworkDogImageContainer
import com.vildanov.randomdog.network.RandomDogApi
import com.vildanov.randomdog.network.asDatabaseModel
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi

class DogImagesRepository(private val dogImagesDatabase: DogImagesDatabase) {

    val dogImages = MutableLiveData(dogImagesDatabase.dogImageDao.getDogImages().value!!.map {
        DogPictureData(1, it.url)
    })

    @KtorExperimentalAPI
    @ExperimentalSerializationApi
    suspend fun refreshDogImages() {
        withContext(Dispatchers.IO) {
            val image = RandomDogApi.getNewDog()
            image?.let {
                dogImagesDatabase.dogImageDao.insert(image.asDatabaseModel())
            }
        }
    }
}