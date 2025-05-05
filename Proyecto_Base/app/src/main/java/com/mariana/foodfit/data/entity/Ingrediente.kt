package com.mariana.foodfit.data.entity

data class Ingrediente(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val fotoUrl: String = "",
    val nutrientes: Map<String, String> = emptyMap()
)