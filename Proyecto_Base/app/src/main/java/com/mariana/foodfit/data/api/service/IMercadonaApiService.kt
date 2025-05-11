package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.CategoriesResponse
import com.mariana.foodfit.data.api.model.ProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz para interactuar con la API de Mercadona.
 */
interface IMercadonaApiService {

    /**
     * Obtiene la lista completa de categorías disponibles.
     */
    @GET("categories/")
    suspend fun getCategories(): CategoriesResponse

    /**
     * Obtiene los productos de una categoría específica por su ID.
     *
     * @param id ID numérico de la categoría.
     * @return Lista de productos pertenecientes a esa categoría.
     */
    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): ProductsResponse
}
