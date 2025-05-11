package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.IApertiumApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente de red para conectarse con la API de Apertium (servicio de traducción).
 */
object ApertiumApiClient {

    private const val BASE_URL = "https://www.apertium.org/apy/"

    // Instancia de la interfaz de servicio de Apertium
    val api: IApertiumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApertiumApiService::class.java)
    }

}