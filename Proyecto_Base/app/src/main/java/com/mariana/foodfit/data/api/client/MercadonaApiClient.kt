package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.IMercadonaApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente de red para conectarse con la API de Mercadona.
 */

object MercadonaApiClient {

    private const val BASE_URL = "https://tienda.mercadona.es/api/"

    // Cliente HTTP personalizado con encabezados y tiempo de espera
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android) FoodFitApp")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        })
        .build()

    // Instancia de la interfaz de servicio de Mercadona
    val mercadonaApi: IMercadonaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // ← Añadir client con headers
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IMercadonaApiService::class.java)
    }

}