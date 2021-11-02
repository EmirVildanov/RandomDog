package com.vildanov.randomdog.repository

import androidx.lifecycle.MutableLiveData
import com.vildanov.randomdog.network.DogPictureData
import com.vildanov.randomdog.database.DatabaseDogImage
import com.vildanov.randomdog.database.DogImagesDatabase
import com.vildanov.randomdog.network.RandomDogApi
import com.vildanov.randomdog.network.asDatabaseModel
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

class DogImagesRepository(private val dogImagesDatabase: DogImagesDatabase) {

    val dogImages = MutableLiveData(listOf<DatabaseDogImage>())

    @KtorExperimentalAPI
    @ExperimentalSerializationApi
    suspend fun refreshDogImages() {
        withContext(Dispatchers.IO) {
            dogImages.postValue(dogImagesDatabase.dogImageDao.getDogImages())
        }
    }

    suspend fun addImage(dogInfo: DogPictureData, name: String, description: String) {
        withContext(Dispatchers.IO) {
            dogImagesDatabase.dogImageDao.insert(dogInfo.asDatabaseModel(
                name = name,
                description = description
            ))
            Timber.i("Inserted images: ${dogImagesDatabase.dogImageDao.getDogImages()}")
            dogImages.postValue(dogImagesDatabase.dogImageDao.getDogImages())
        }
    }
}