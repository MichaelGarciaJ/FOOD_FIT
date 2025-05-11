package com.mariana.foodfit.data.entity

import com.mariana.foodfit.data.init.IngredientePlatillo

/**
 * Modelo de datos que representa un platillo completo en el sistema, incluyendo ingredientes y pasos.
 *
 * Este modelo es el que se guarda en Firestore y se utiliza para mostrar
 * todos los detalles del platillo.
 */
data class Platillo(
    var idPlatillo: String = "",
    var nombre: String = "",
    var categoria: String = "",
    var pasosPreparacion: List<String> = emptyList(),
    var fotoUrl: String = "",
    var creadoPor: String = "",
    var ingredientes: List<IngredientePlatillo> = emptyList(),
    var isFavorite: Boolean = false
)
