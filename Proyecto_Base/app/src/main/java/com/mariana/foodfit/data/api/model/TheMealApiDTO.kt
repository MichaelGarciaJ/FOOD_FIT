package com.mariana.foodfit.data.api.model

/**
 * Modelo de respuesta principal de TheMealDB.
 * Puede contener una lista de platillos (meals) o ser null si no hay resultados.
 */
data class MealResponse(
    val meals: List<Meal>?
)

/**
 * Representa un platillo individual dentro de la respuesta.
 */
data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String
)
