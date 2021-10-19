package com.vildanov.randomdog.network

import android.app.Activity
import com.vildanov.randomdog.RandomDogApplication
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager


abstract class NetworkService {

    companion object {
        val jsonFormat = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        const val RESPONSE_CONTENT_READ_LIMIT = 500

        const val BASE_HTTP_URL = "https://fighting-game-server.herokuapp.com"
        const val BASE_WS_URL = "ws://fighting-game-server.herokuapp.com"

        const val AUTHORIZATION_HEADER_NAME = "Authorization"

        const val TICKET_QUERY_PARAM_KEY = "ticket"
        const val ID_QUERY_PARAM_KEY = "id"
        const val USERNAME_QUERY_PARAM_KEY = "username"
    }


    @KtorExperimentalAPI
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun getSuccessfulResponseOrException(
        application: RandomDogApplication? = null,
        activity: Activity? = null,
        funcBody: suspend () -> HttpResponse
    ): HttpResponse? {
        val response: HttpResponse

        if (application == null) {
            Timber.e("Null application")
        }
        if (application != null) {
            if (!application.isInternetAvailable(activity!!)) {
                Timber.e("ENABLE YOUR INTERNET CONNECTION")
                return null
            }
        }
        try {
            response = funcBody()
            when {
                responseIsSuccessful(response) -> return response
                response.status == HttpStatusCode.Unauthorized -> throw NetworkException("Unauthorized")
                else -> {
                    Timber.e("Unknown server exception")
                    throw UserNetworkException(
                        response.content.readUTF8Line(400) ?: "Unknown exception"
                    )
                }
            }
        } catch (exception: UnknownHostException) {
            Timber.e("Enable to connect to server")
            throw NetworkException("Enable to connect to server")
        } catch (exception: IOException) {
            Timber.e("Something wrong with server connection")
            Timber.e(exception)
            throw NetworkException("Check your Internet connection and try again")
        } catch (e: Exception) {
            Timber.e("Unknown exception")
            throw e
        }
    }

    private fun responseIsSuccessful(httpResponse: HttpResponse): Boolean {
        return httpResponse.status.value in 200..299
    }

    open class NetworkException(message: String) : IllegalArgumentException(message)
    class UserNetworkException(message: String) : NetworkException(message)
}

object CustomTrustManager : X509TrustManager {
    override fun checkClientTrusted(
        chain: Array<X509Certificate?>?,
        authType: String?
    ) {
        chain?.forEach {
            Timber.i("Certificate checkClientTrusted: $it")
        }
    }

    override fun checkServerTrusted(
        chain: Array<X509Certificate?>?,
        authType: String?
    ) {
        chain?.forEach {
            Timber.i("Certificate checkServerTrusted: $it")
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}