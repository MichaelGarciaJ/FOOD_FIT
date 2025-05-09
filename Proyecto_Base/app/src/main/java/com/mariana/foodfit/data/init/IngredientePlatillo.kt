package com.mariana.foodfit.data.init

data class IngredientePlatillo(
    val nombre: String = "",
    val cantidad: Int = 0,
    val unidad: String = ""
) {
    // Constructor vacío para la deserialización de Firestore
    constructor() : this("", 0, "")
}
