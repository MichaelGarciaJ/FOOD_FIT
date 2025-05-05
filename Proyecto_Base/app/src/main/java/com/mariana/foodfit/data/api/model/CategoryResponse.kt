package com.mariana.foodfit.data.api.model

data class CategoryResponse(
    val id: String,
    val name: String,
    val categories: List<CategoryResponse>? = null    // <-- añadimos aquí
)