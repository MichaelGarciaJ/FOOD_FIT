package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.ApertiumApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio que utiliza la API de Apertium para traducir texto del español al inglés.
 */
class ApertiumApiService {

    private val api = ApertiumApiClient.api

    /**
     * Método que traduce el texto proporcionado al inglés.
     *
     * @param texto Texto en español a traducir.
     * @return Texto traducido al inglés o el original si ocurre un error.
     */
    suspend fun traducirATextoIngles(texto: String): String = withContext(Dispatchers.IO) {
        try {
            val response = api.translate(texto)
            val translated = response.responseData.translatedText
            Log.d("Apertium", "Traducido '$texto' → '$translated'")
            translated
        } catch (e: Exception) {
            Log.e("Apertium", "Error al traducir '$texto': ${e.message}")
            texto
        }
    }
}
