package com.mariana.foodfit.data.api.client

import com.mariana.foodfit.data.api.service.ITheMealApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TheMealApiClient {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: ITheMealApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITheMealApiService::class.java)
    }
}