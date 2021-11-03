package com.vildanov.randomdog.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://dog.ceo/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RandomDogService {
    @GET("breeds/image/random")
    suspend fun getProperties(): DogImageProperty
}

object RandomDogApi {
    val retrofitService : RandomDogService by lazy { retrofit.create(RandomDogService::class.java) }
}