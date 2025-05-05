package com.mariana.foodfit.data.entity

data class FoodItem(
    val id: String,
    val fotoUrl: String,
    val title: String,
    val subtitle: String,
    var isFavorite: Boolean
)
