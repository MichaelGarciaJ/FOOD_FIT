package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.MercadonaApiClient
import com.mariana.foodfit.data.api.model.ProductDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que busca un producto por nombre en la API de Mercadona.
 */
class MercadonaApiService {
    private val api = MercadonaApiClient.mercadonaApi

    suspend fun buscarIngredientePorNombre(nombreBuscado: String): Map<String, Any>? = withContext(Dispatchers.IO) {
        Log.d("MercadonaApi", "Inicio búsqueda: $nombreBuscado")
        val cleanTarget = limpiar(nombreBuscado)

        val producto = buscarProductoEnCategorias(cleanTarget)
        if (producto == null) {
            Log.w("MercadonaApi", "No hallado: $nombreBuscado")
            return@withContext null
        }

        val ingredienteMap = mapOf(
            "idIngrediente" to producto.id,
            "nombre" to producto.display_name,
            "precio" to producto.price_instructions.bulk_price,
            "fotoUrl" to producto.thumbnail,
        )

        Log.d("MercadonaApi", "Ingrediente encontrado: $ingredienteMap")
        ingredienteMap
    }
    private suspend fun buscarProductoEnCategorias(cleanTarget: String): ProductDetailResponse? {
        val rootCategories = api.getCategories().results

        for (cat in rootCategories) {
            for (sub in cat.categories) {
                val detail = api.getCategoryById(sub.id.toInt())
                for (leaf in detail.categories) {
                    for (product in leaf.products) {
                        if (limpiar(product.display_name) == cleanTarget) {
                            return product
                        }
                    }
                }
            }
        }
        return null
    }

    private fun limpiar(s: String): String =
        s.trim().removeSurrounding("\"").replace(Regex("\\s+"), " ").lowercase()
}

