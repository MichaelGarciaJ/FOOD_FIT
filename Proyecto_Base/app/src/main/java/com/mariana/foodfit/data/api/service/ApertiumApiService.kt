package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.ApertiumApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApertiumApiService {

    private val api = ApertiumApiClient.api

    suspend fun traducirATextoIngles(texto: String): String = withContext(Dispatchers.IO) {
        try {
            val response = api.translate(texto)
            val translated = response.responseData.translatedText
            Log.d("Apertium", "Traducido '$texto' → '$translated'")
            translated
        } catch (e: Exception) {
            Log.e("Apertium", "Error al traducir: ${e.message}")
            texto
        }
    }

}