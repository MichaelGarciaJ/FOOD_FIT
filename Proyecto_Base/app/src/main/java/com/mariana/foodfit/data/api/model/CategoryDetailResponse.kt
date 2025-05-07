package com.mariana.foodfit.data.api.model

data class CategoryDetailResponse(
    val id: String,
    val categories: List<ProductsResponse>
)