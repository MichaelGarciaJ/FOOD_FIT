package com.mariana.foodfit.data.api.model

data class CategoryDetailResponse(
    val id: String,
    val name: String,
    val categories: List<CategoryResponse>?,  // Subcategorías (pueden no funcionar)
    val products: List<ProductResponse>?      // Productos directos en esta categoría
)