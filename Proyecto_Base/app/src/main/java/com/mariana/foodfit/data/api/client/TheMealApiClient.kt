package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.ITheMealApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente de red para conectarse con la API de TheMealDB.
 */
object TheMealApiClient {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    // Instancia de la interfaz de servicio de TheMealDB
    val api: ITheMealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITheMealApiService::class.java)
    }
}