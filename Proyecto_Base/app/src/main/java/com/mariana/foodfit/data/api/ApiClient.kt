package com.mariana.foodfit.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://tienda.mercadona.es/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android) FoodFitApp")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        })
        .build()

    val mercadonaApi: MercadonaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // ← Añadir client con headers
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MercadonaApiService::class.java)
    }
}