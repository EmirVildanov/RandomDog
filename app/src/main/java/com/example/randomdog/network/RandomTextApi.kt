package com.example.randomdog.network

import com.example.randomdog.data.DownloadedTextData
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import java.lang.Exception

object RandomTextApi : NetworkService() {
    @ExperimentalSerializationApi
    @KtorExperimentalAPI
    suspend fun getNewText(): DownloadedTextData? {
        try {
            val url = "https://api.seazon.org/1-0-1-1-1-0/0-0-1/2-9-15-25-1-1/api.txt"
            val response =  getSuccessfulResponseOrException {
                client.get() {
                    url(url)
                    contentType(ContentType.Application.Json)
                }
            } ?: return null
            return DownloadedTextData(status = "Succeed", text = response.receive())
        } catch (e: TooLongLineException) {
            Timber.e(e.message)
            throw e
        } catch (e: Exception) {
            Timber.e(e.message)
            throw e
        }
    }
}