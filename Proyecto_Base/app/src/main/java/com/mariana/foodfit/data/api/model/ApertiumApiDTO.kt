package com.mariana.foodfit.data.api.model

data class ApertiumResponse(
    val responseData: ResponseData,
    val responseStatus: Int
)

data class ResponseData(
    val translatedText: String
)