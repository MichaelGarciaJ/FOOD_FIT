package com.mariana.foodfit.data.entity

data class FoodItem(
    val imageResId: Int,
    val title: String,
    val subtitle: String,
    var isFavorite: Boolean
)
