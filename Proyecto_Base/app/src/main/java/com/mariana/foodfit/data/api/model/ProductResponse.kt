package com.mariana.foodfit.data.api.model

data class ProductResponse(
    val id: String,
    val name: String,
    val price: Double,
    val image: String,
    val nutrition: NutritionInfo?
)