package com.mariana.foodfit.data.api


import retrofit2.http.GET
import retrofit2.http.Path

interface IMercadonaApiService {
    @GET("categories/")
    suspend fun getCategories(): Map<String, Any>

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Map<String, Any>
}

