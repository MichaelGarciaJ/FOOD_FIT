package com.mariana.foodfit.data.entity

data class Ingrediente(
    val idIngrediente: String,
    val nombre: String = "",
    val precio: Double = 0.0,
    val fotoUrl: String = "",
    val calorias: Double = 0.0,
    val proteinas: Double = 0.0,
    val grasas : Double = 0.0,
    val carbohidratos: Double = 0.0,
    val fibra: Double = 0.0
)