package com.mariana.foodfit.data.api

import com.mariana.foodfit.data.api.model.CategoriesResponse
import com.mariana.foodfit.data.api.model.CategoryDetailResponse
import com.mariana.foodfit.data.api.model.CategoryResponse
import com.mariana.foodfit.data.api.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MercadonaApiService {

    @GET("categories/")
    suspend fun getCategories(): CategoriesResponse

    @GET("categories/{categoryId}")
    suspend fun getCategoryById(@Path("categoryId") categoryId: String): CategoryDetailResponse
}
