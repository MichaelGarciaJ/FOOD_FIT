package com.mariana.foodfit.data.api.model

data class ProductDetailResponse (
    val id: String,
    val thumbnail: String,
    val display_name: String,
    val price_instructions: PriceProduct
)