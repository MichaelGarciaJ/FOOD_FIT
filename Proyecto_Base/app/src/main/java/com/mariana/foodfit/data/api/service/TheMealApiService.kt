package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.TheMealApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TheMealApiService {

    private val api = TheMealApiClient.api

    suspend fun obtenerFotoPorNombre(nombre: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMealByName(nombre)
            val meal = response.meals?.firstOrNull()
            val fotoUrl = meal?.strMealThumb
            Log.d("TheMealAPi", "Foto para '$nombre': $fotoUrl")
            fotoUrl
        } catch (e: Exception) {
            Log.e("TheMealAPi", "Error al obtener foto: ${e.message}")
            null
        }
    }
}