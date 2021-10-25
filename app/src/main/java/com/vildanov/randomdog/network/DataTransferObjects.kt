package com.vildanov.randomdog.network

import com.vildanov.randomdog.data.DogPictureData
import com.vildanov.randomdog.database.DatabaseDogImage

data class NetworkDogImageContainer(val dogImage: DogPictureData)

data class NetworkDogImage(
    val description: String,
    val url: String,
    val dateOfAdding: String,
    val thumbnail: String,
    val closedCaptions: String?)

fun NetworkDogImageContainer.asDatabaseModel(): DatabaseDogImage {
    return dogImage.let {
        DatabaseDogImage(
            description = "test_description",
            url = it.url,
            dateOfAdding = "test_date",
            thumbnail = "test_thumbnail")
    }
}

fun DogPictureData.asDatabaseModel(): DatabaseDogImage {
    return DatabaseDogImage(
            description = "test_description",
            url = url,
            dateOfAdding = "test_date",
            thumbnail = "test_thumbnail")
}
