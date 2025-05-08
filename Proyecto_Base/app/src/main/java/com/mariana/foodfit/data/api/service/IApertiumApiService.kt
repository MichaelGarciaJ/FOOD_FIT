package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.ApertiumResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IApertiumApiService {
    @GET("translate")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "es|en"
    ): ApertiumResponse
}