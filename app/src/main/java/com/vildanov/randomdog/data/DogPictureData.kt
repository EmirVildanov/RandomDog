package com.vildanov.randomdog.data

import kotlinx.serialization.Serializable

@Serializable
data class DogPictureData(val fileSizeBytes: Int, val url : String)
