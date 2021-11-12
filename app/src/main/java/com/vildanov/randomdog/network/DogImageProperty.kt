package com.vildanov.randomdog.network

import com.squareup.moshi.Json
import com.vildanov.randomdog.database.DatabaseDogImage

data class DogImageProperty(
    val status: String,
    @Json(name = "message") val url: String
)

fun DogImageProperty.asDatabaseModel(name: String, description: String): DatabaseDogImage {
    return DatabaseDogImage(
        url = url,
        name = name,
        description = description
    )
}