package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.MercadonaApiClient
import com.mariana.foodfit.data.api.model.ProductDetailResponse
import com.mariana.foodfit.data.entity.Ingrediente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que busca un producto por nombre en la API de Mercadona.
 * Lleva un contador en memoria de cuántas veces se ha buscado cada nombre,
 * y cuando supera el umbral (3) lo guarda en Firestore usando IngredienteService.
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
            "fotoIngrediente" to producto.thumbnail,
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

    private fun convertirAModeloIngrediente(producto: ProductDetailResponse): Ingrediente {
        return Ingrediente(
            idIngrediente = producto.id,
            nombre = producto.display_name,
            precio = producto.price_instructions.bulk_price,
            fotoUrl = producto.thumbnail,
            nutrientes = mapOf(
                "calorias" to "0",
                "proteinas" to "0",
                "carbohidratos" to "0",
                "grasas" to "0"
            )
        )
    }

    private fun limpiar(s: String): String =
        s.trim().removeSurrounding("\"").replace(Regex("\\s+"), " ").lowercase()
}

