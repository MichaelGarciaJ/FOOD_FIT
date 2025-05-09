package com.mariana.foodfit.data.init

data class PlatilloInicial(
    val nombre: String,
    val categoria: String,
    val ingredientes: List<IngredientePlatillo>,
    val pasos: List<String>
)