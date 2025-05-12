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

    /**
     * Método que busca un ingrediente por su nombre y retorna su información básica.
     *
     * @param nombreBuscado Nombre del ingrediente a buscar.
     * @return Mapa con ID, nombre, precio y URL de la imagen del ingrediente (o null si no se encuentra).
     */
    suspend fun buscarIngredientePorNombre(nombreBuscado: String): Map<String, Any>? =
        withContext(Dispatchers.IO) {
            Log.d("MercadonaApi", "Inicio búsqueda: $nombreBuscado")
            val cleanTarget = limpiar(nombreBuscado)

            val producto = buscarProductoEnCategorias(cleanTarget)
            if (producto == null) {
                Log.w("MercadonaApi", "No se encontró el producto: $cleanTarget")
                return@withContext null
            }

            val ingredienteMap = mapOf(
                "idIngrediente" to producto.id,
                "nombre" to producto.display_name,
                "precio" to producto.price_instructions.unit_price,
                "fotoUrl" to producto.thumbnail,
            )

            Log.d("MercadonaApi", "Ingrediente encontrado: $ingredienteMap")
            ingredienteMap
        }

    /**
     * Método que busca el producto dentro de las categorías y subcategorías de Mercadona.
     *
     * @param cleanTarget Nombre limpio del producto a buscar.
     * @return Detalle del producto si se encuentra, null si no.
     */
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

    /**
     * Método que limpia el texto eliminando espacios redundantes y convirtiéndolo a minúsculas.
     *
     * @param s Texto original.
     * @return Texto limpio.
     */
    private fun limpiar(s: String): String =
        s.trim().removeSurrounding("\"").replace(Regex("\\s+"), " ").lowercase()
}
