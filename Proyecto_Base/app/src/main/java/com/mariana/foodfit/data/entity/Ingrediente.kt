package com.mariana.foodfit.data.entity

/**
 * Modelo de datos que representa un ingrediente individual con información nutricional y económica.
 *
 * Este modelo se guarda en Firestore para representar ingredientes únicos.
 */
data class Ingrediente(
    var idIngrediente: String = "",
    var nombre: String = "",
    var precio: Double = 0.0,
    var fotoUrl: String = "",
    var calorias: Double = 0.0,
    var proteinas: Double = 0.0,
    var grasas: Double = 0.0,
    var carbohidratos: Double = 0.0,
    var fibra: Double = 0.0
)