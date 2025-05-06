package com.mariana.foodfit.data.api

import com.mariana.foodfit.data.api.model.CategoriesResponse
import com.mariana.foodfit.data.api.model.CategoryDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface IMercadonaApiService {

    @GET("categories/")
    suspend fun getCategories(): CategoriesResponse

    @GET("categories/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: String): CategoryDetailResponse
}
