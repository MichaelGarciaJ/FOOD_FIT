package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.TheMealApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que obtiene la imagen de un platillo a partir de su nombre usando la API de TheMealDB.
 */
class TheMealApiService {

    private val api = TheMealApiClient.api

    /**
     * Método que devuelve la URL de la foto del platillo si existe.
     *
     * @param nombre Nombre del platillo.
     * @return URL de la imagen o null si no se encuentra o ocurre un error.
     */
    suspend fun obtenerFotoPorNombre(nombre: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMealByName(nombre)
            val meal = response.meals?.firstOrNull()
            val fotoUrl = meal?.strMealThumb

            if (fotoUrl == null) {
                Log.w("TheMealApi", "No se encontró foto para '$nombre'")
            }

            Log.d("TheMealAPi", "Foto para '$nombre': $fotoUrl")
            fotoUrl
        } catch (e: Exception) {
            Log.e("TheMealAPi", "Error al obtener foto: ${e.message}")
            null
        }
    }
}
