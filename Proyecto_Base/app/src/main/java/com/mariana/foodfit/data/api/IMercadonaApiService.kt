package com.mariana.foodfit.data.api


import com.mariana.foodfit.data.api.model.CategoriesResponse
import com.mariana.foodfit.data.api.model.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface IMercadonaApiService {
    @GET("categories/")
    suspend fun getCategories(): CategoriesResponse

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): ProductsResponse
}
