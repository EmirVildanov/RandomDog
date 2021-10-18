package com.example.randomdog.network

import com.example.randomdog.data.DogPictureData
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import timber.log.Timber

object RandomDogApi : NetworkService() {
    @ExperimentalSerializationApi
    @KtorExperimentalAPI
    suspend fun getNewDog(): DogPictureData? {
        val response = getSuccessfulResponseOrException {
            client.get() {
                url("https://random.dog/woof.json")
                contentType(ContentType.Application.Json)
            }
        } ?: return null
        try {
            val stringResponse = response.content.readUTF8Line(RESPONSE_CONTENT_READ_LIMIT) ?: ""
            return jsonFormat.decodeFromString(stringResponse)
        } catch (e: TooLongLineException) {
            Timber.e(response.content.readUTF8Line(RESPONSE_CONTENT_READ_LIMIT) ?: "")
            throw e
        }
    }
}