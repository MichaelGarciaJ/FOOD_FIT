package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.ApertiumResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para interactuar con la API de Apertium (traducción de texto).
 */
interface IApertiumApiService {

    /**
     * Traduce un texto del español al inglés utilizando el endpoint 'translate'.
     *
     * @param text Texto en español que se desea traducir.
     * @param langPair Idioma de origen y destino (por defecto "es|en").
     * @return Respuesta con el texto traducido.
     */
    @GET("translate")
    suspend fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String = "es|en"
    ): ApertiumResponse
}
