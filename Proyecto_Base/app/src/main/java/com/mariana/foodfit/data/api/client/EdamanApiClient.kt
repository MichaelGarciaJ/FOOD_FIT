package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.IEdamanApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente de red para conectarse con la API de Edamam (información nutricional).
 */
object EdamanApiClient {
    private const val BASE_URL = "https://api.edamam.com/"

    // Cliente HTTP con configuración de tiempos y headers personalizados
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android) FoodFitApp")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    // Instancia de la interfaz de servicio de Edamam
    val edamamApi: IEdamanApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IEdamanApiService::class.java)
    }

}