package com.vildanov.randomdog.network

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import timber.log.Timber
import java.lang.Exception

object RandomDogApi : NetworkService() {
    @ExperimentalSerializationApi
    @KtorExperimentalAPI
    suspend fun getNewDog(): DogPictureData? {
        val response = getSuccessfulResponseOrException {
            client.get {
                url("https://dog.ceo/api/breed/hound/images/random")
                contentType(ContentType.Application.Json)
            }
        } ?: return null
        try {
            val stringResponse = response.content.readUTF8Line(RESPONSE_CONTENT_READ_LIMIT) ?: ""
            return jsonFormat.decodeFromString(stringResponse)
        } catch (exception: TooLongLineException) {
            Timber.e(response.content.readUTF8Line(RESPONSE_CONTENT_READ_LIMIT) ?: "")
            throw exception
        } catch (exception: Exception) {
            Timber.e(exception)
            throw exception
        }
    }
}