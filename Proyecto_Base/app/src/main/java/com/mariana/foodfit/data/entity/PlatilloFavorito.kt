package com.mariana.foodfit.data.entity

/**
 * Modelo de datos que representa un platillo marcado como favorito por un usuario.
 */
data class PlatilloFavorito(
    val platilloId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)