package com.vildanov.randomdog.network

import com.vildanov.randomdog.database.DatabaseDogImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DogPictureData(
    val status: String,
    @SerialName("message") val url: String
)

fun DogPictureData.asDatabaseModel(name: String, description: String): DatabaseDogImage {
    return DatabaseDogImage(
        url = url,
        name = name,
        description = description
    )
}
