package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.EdamanApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que utiliza la API de Edamam para obtener información nutricional de un ingrediente.
 */
class EdamanApiService {
    private val api = EdamanApiClient.edamamApi
    private val appId: String = "d2374919"
    private val appKey: String = "db94865ca22c0e9efd27483378cef763"

    /**
     * Método que obtiene un mapa con los nutrientes del ingrediente dado.
     *
     * @param nombre Nombre del ingrediente a buscar.
     * @return Mapa con calorías, proteínas, grasas, carbohidratos y fibra.
     */
    suspend fun obtenerNutrientes(nombre: String): Map<String, Double> =
        withContext(Dispatchers.IO) {
            val response = api.searchFood(appId, appKey, nombre)

            val nutrientes = response.parsed.firstOrNull()?.food?.nutrients
                ?: response.hints.firstOrNull()?.food?.nutrients

            if (nutrientes == null) {
                Log.w("Edaman", "No se encontraron nutrientes para '$nombre'")
            }

            val result = mapOf(
                "calorias" to (nutrientes?.ENERC_KCAL ?: 0.0),
                "proteinas" to (nutrientes?.PROCNT ?: 0.0),
                "grasas" to (nutrientes?.FAT ?: 0.0),
                "carbohidratos" to (nutrientes?.CHOCDF ?: 0.0),
                "fibra" to (nutrientes?.FIBTG ?: 0.0)
            )

            Log.d("Edaman", "Nutrientes Map para '$nombre': $result")
            result
        }
}
