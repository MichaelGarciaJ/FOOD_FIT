package com.mariana.foodfit.data.service

import android.util.Log
import com.mariana.foodfit.data.api.ApiClient
import com.mariana.foodfit.data.api.model.ProductDetailResponse
import com.mariana.foodfit.data.entity.Ingrediente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que busca un producto por nombre en la API de Mercadona.
 * Lleva un contador en memoria de cuántas veces se ha buscado cada nombre,
 * y cuando supera el umbral (3) lo guarda en Firestore usando IngredienteService.
 */
class MercadonaApiService(
    private val ingredienteService: IngredienteService
) {
    private val api = ApiClient.mercadonaApi

    object BusquedaContador {
        val contador = mutableMapOf<String, Int>()
    }

    suspend fun buscarYGuardarSiCorresponde(nombreBuscado: String) = withContext(Dispatchers.IO) {
        Log.d("Busqueda", "Inicio búsqueda: $nombreBuscado")
        val cleanTarget = limpiar(nombreBuscado)

        val producto = buscarProductoEnCategorias(cleanTarget)
        if (producto == null) {
            Log.w("Busqueda", "No hallado: $nombreBuscado")
            return@withContext
        }

        val veces = incrementarContador(nombreBuscado)
        if (veces >= 3) {
            val ingrediente = convertirAModeloIngrediente(producto)
            val newId = ingredienteService.addIngrediente(ingrediente)
            Log.d("Busqueda", "Ingrediente guardado con ID $newId")
        }
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

    private fun incrementarContador(nombre: String): Int {
        val veces = (BusquedaContador.contador[nombre] ?: 0) + 1
        BusquedaContador.contador[nombre] = veces
        Log.d("Busqueda", "Veces buscado '$nombre': $veces")
        return veces
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

