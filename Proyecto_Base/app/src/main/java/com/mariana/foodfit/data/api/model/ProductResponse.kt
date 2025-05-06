package com.mariana.foodfit.data.api.model

data class ProductResponse(
    val id: String,
    val display_name: String,
    val thumbnail: String,
    val price_instructions: String
)