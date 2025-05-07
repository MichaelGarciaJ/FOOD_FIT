package com.mariana.foodfit.data.api.model

// Response principal de categorías
data class CategoriesResponse(
    val results: List<CategoryResponse>
)

// Categoría que contiene más detalles (subcategorías)
data class CategoryResponse(
    val categories: List<CategoryDetailResponse>
)

// Detalle de cada subcategoría de una categoría
data class CategoryDetailResponse(
    val id: String,
    val categories: List<ProductsResponse>
)

// Respuesta de productos en una categoría
data class ProductsResponse(
    val categories: List<ProductResponse>
)

// Respuesta de los productos de una subcategoría
data class ProductResponse(
    val products: List<ProductDetailResponse>
)

// Detalle del producto individual
data class ProductDetailResponse(
    val id: String,
    val display_name: String, // Nombre del producto
    val thumbnail: String, // URL de la imagen
    val price_instructions: PriceProduct // Precio del producto
)

// Precio del producto
data class PriceProduct(
    val bulk_price: Double
)
