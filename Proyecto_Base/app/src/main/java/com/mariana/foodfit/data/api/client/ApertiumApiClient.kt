package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.IApertiumApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApertiumApiClient {

    private const val BASE_URL = "https://www.apertium.org/apy/"

    val api: IApertiumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApertiumApiService::class.java)
    }
}