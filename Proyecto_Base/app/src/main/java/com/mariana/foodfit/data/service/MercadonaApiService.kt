package com.mariana.foodfit.data.service

import android.util.Log
import com.mariana.foodfit.data.api.ApiClient
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


    /**
     * Busca en todas las subcategorías de todas las categorías raíz.
     * Cuando encuentra el producto, incrementa contador y, si >=3, lo persiste.
     */
    suspend fun buscarYGuardarSiCorresponde(nombreBuscado: String) = withContext(Dispatchers.IO) {
        Log.d("Busqueda", "Inicio búsqueda: $nombreBuscado")
        val cleanTarget = limpiar(nombreBuscado)

        // Obtener lista de categorías raíz
        val root = api.getCategories()["results"] as? List<Map<String,Any>> ?: return@withContext

        // Iterar categorías y subcategorías
        var encontrado: Map<String,Any>? = null
        outer@ for (cat in root) {
            val subs = cat["categories"] as? List<Map<String,Any>> ?: continue
            for (sub in subs) {
                val id = sub["id"] as? Double ?: continue
                val detail = api.getCategoryById(id.toInt())
                val subcats = detail["categories"] as? List<Map<String,Any>> ?: continue
                for (leaf in subcats) {
                    val products = leaf["products"] as? List<Map<String,Any>> ?: continue
                    for (p in products) {
                        val rawName = p["display_name"] as? String ?: ""
                        if (limpiar(rawName) == cleanTarget) {
                            encontrado = p
                            break@outer
                        }
                    }
                }
            }
        }

        if (encontrado == null) {
            Log.w("Busqueda", "No hallado: $nombreBuscado")
            return@withContext
        }

        // manejar contador
        val veces = (BusquedaContador.contador[nombreBuscado] ?: 0) + 1
        BusquedaContador.contador[nombreBuscado] = veces
        Log.d("Busqueda", "Veces buscado '$nombreBuscado': $veces")

        if (veces >= 3) {
            // convertir Map a fields
            val id = encontrado["id"] as String
            val display = encontrado["display_name"] as String
            val thumb = encontrado["thumbnail"] as? String ?: ""
            val price = (encontrado["price_instructions"] as? Map<String,Any>)
                ?.get("bulk_price")
                .toString()
                .toDoubleOrNull() ?: 0.0

            val ingrediente = Ingrediente(
                idIngrediente = id,
                nombre = display,
                precio = price,
                fotoUrl = thumb,
                nutrientes = mapOf(
                    "calorias" to "0",
                    "proteinas" to "0",
                    "carbohidratos" to "0",
                    "grasas" to "0"
                )
            )

            val newId = ingredienteService.addIngrediente(ingrediente)
            Log.d("Busqueda", "Ingrediente guardado con ID $newId")
        }
    }

    private fun limpiar(s: String): String =
        s.trim().removeSurrounding("\"").replace(Regex("\\s+"), " ").lowercase()
}
