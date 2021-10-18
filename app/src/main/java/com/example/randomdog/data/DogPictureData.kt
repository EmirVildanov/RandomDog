package com.example.randomdog.data

import kotlinx.serialization.Serializable

@Serializable
data class DogPictureData(val fileSizeBytes: Int, val url : String)
