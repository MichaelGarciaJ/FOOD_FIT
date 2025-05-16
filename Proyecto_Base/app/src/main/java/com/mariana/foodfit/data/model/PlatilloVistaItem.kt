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

data class Ingredient(
    var cantidad: Int,
    var nombre: String,
    var unidad: String,
    var precio: Double
)

data class IngredientDetail(
    val nombre: String,
    val calorias: Double,
    val grasas: Double,
    val carbohidratos: Double,
    val fibra: Double,
    val proteina: Double
)

data class PreparationStep(
    var numero: Int,
    var texto: String
)
