package com.mariana.foodfit.data.api.model

/**
 * Modelo de respuesta principal de la API de Edamam.
 * Contiene listas de alimentos parseados y sugerencias (hints).
 */
data class EdamamResponse(
    val parsed: List<EdamamParsed>,
    val hints: List<EdamamHint>
)

/**
 * Elemento parseado directamente desde el texto ingresado.
 */
data class EdamamParsed(
    val food: EdamamFood
)

/**
 * Sugerencias de alimentos relacionadas con el texto ingresado.
 */
data class EdamamHint(
    val food: EdamamFood
)

/**
 * Representa un alimento con su nombre y sus nutrientes.
 */
data class EdamamFood(
    val label: String,
    val nutrients: EdamamNutrients
)

/**
 * Información nutricional del alimento.
 * Algunos valores pueden ser nulos si la API no los proporciona.
 */
data class EdamamNutrients(
    val ENERC_KCAL: Double?,
    val PROCNT: Double?,
    val FAT: Double?,
    val CHOCDF: Double?,
    val FIBTG: Double?
)
