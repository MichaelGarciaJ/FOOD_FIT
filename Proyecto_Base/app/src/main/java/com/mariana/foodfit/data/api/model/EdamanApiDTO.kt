package com.mariana.foodfit.data.api.model

data class EdamamResponse(
    val parsed: List<EdamamParsed>,
    val hints: List<EdamamHint>
)

data class EdamamParsed(
    val food: EdamamFood
)

data class EdamamHint(
    val food: EdamamFood
)

data class EdamamFood(
    val label: String,
    val nutrients: EdamamNutrients
)

data class EdamamNutrients(
    val ENERC_KCAL: Double?,
    val PROCNT: Double?,
    val FAT: Double?,
    val CHOCDF: Double?,
    val FIBTG: Double?
)

