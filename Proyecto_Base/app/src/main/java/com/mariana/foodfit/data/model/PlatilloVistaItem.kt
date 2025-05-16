package com.mariana.foodfit.data.model

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

/**
 * Modelo que representa un ingrediente básico con cantidad y precio.
 */
data class Ingredient(
    var cantidad: Int,
    var nombre: String,
    var unidad: String,
    var precio: Double
)

/**
 * Modelo detallado de un ingrediente que incluye información nutricional.
 */
data class IngredientDetail(
    val nombre: String,
    val calorias: Double,
    val grasas: Double,
    val carbohidratos: Double,
    val fibra: Double,
    val proteina: Double
)

/**
 * Modelo de paso de preparación de un platillo, con un número de orden y texto descriptivo.
 *
 */
data class PreparationStep(
    var numero: Int,
    var texto: String
)
