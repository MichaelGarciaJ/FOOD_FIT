package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITheMealApiService {
    @GET("search.php")
    suspend fun searchMealByName(
        @Query("s") name: String
    ): MealResponse
}