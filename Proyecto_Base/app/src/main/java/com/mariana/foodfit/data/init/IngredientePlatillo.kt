package com.mariana.foodfit.data.init

/**
 * Modelo de datos que representa un ingrediente dentro del contexto de un platillo específico.
 *
 * Este modelo se utiliza para definir qué ingredientes componen un platillo,
 * junto con la cantidad y la unidad correspondiente.
 */
data class IngredientePlatillo(
    var nombre: String = "",
    var cantidad: Int = 0,
    var unidad: String = ""
)
