package com.mariana.foodfit.data.entity

import com.mariana.foodfit.data.init.IngredientePlatillo

data class Platillo(
    val idPlatillo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val pasosPreparacion: List<String> = emptyList(),
    val fotoUrl: String = "",
    val creadoPor: String = "",
    val ingredientes: List<IngredientePlatillo> = emptyList(),
    var isFavorite: Boolean = false
)
