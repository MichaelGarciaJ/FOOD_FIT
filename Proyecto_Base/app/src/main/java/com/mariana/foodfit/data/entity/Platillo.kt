package com.mariana.foodfit.data.entity

data class Platillo(
    val idPlatillo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val ingredientes: List<String> = emptyList(),
    val pasosPreparacion: List<String>? = emptyList(),
    val nutricion: Map<String, Any>? = emptyMap(), // Cambié de String a Any
    val fotoUrl: String? = "",
    val creadoPor: String? = null,
    var isFavorite: Boolean = false
)
