package com.mariana.foodfit.data.api.model

/**
 * Respuesta principal de categorías obtenidas de la API de Mercadona.
 */
data class CategoriesResponse(
    val results: List<CategoryResponse>
)

/**
 * Cada categoría puede tener una lista de subcategorías.
 */
data class CategoryResponse(
    val categories: List<CategoryDetailResponse>
)

/**
 * Subcategorías dentro de una categoría principal.
 */
data class CategoryDetailResponse(
    val id: String,
    val categories: List<ProductsResponse>
)

/**
 * Contiene subcategorías que agrupan productos.
 */
data class ProductsResponse(
    val categories: List<ProductResponse>
)

/**
 * Lista de productos disponibles dentro de una subcategoría.
 */
data class ProductResponse(
    val products: List<ProductDetailResponse>
)

/**
 * Detalle individual de un producto.
 */
data class ProductDetailResponse(
    val id: String,
    val display_name: String,  // Nombre del producto
    val thumbnail: String,     // URL de imagen del producto
    val price_instructions: PriceProduct  // Información de precio
)

/**
 * Información de precio del producto, incluyendo precio por unidad.
 */
data class PriceProduct(
    val unit_price: Double
)