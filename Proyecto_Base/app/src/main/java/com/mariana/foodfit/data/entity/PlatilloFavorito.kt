package com.mariana.foodfit.data.entity

data class PlatilloFavorito(
    val platilloId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)