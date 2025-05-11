package com.mariana.foodfit.data.entity

/**
 * Modelo de datos que representa un platillo en la vista de lista de platillos (UI).
 */
data class PlatilloVistaItem(
    val id: String,
    val fotoUrl: String,
    val title: String,
    val subtitle: String,
    var isFavorite: Boolean
)