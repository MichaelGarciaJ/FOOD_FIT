package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.EdamamResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IEdamanApiService {
    @GET("api/food-database/v2/parser")
    suspend fun searchFood(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") ingredient: String
    ): EdamamResponse

}