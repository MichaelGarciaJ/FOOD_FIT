package com.mariana.foodfit.data.api.model

/**
 * Modelo de respuesta de la API de Apertium (servicio de traducción).
 */
data class ApertiumResponse(
    val responseData: ResponseData,
    val responseStatus: Int
)

/**
 * Contiene el texto traducido como parte de la respuesta.
 */
data class ResponseData(
    val translatedText: String
)