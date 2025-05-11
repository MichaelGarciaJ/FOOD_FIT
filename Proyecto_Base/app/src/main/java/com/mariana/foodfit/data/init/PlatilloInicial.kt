package com.mariana.foodfit.data.init

/**
 * Modelo de datos para definir un platillo inicial.
 *
 * Este modelo se utiliza para poblar la base de datos con información predeterminada
 * al iniciar la aplicación por primera vez.
 */
data class PlatilloInicial(
    val nombre: String,
    val categoria: String,
    val ingredientes: List<IngredientePlatillo>,
    val pasos: List<String>
)