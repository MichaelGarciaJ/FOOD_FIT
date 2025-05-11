package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.EdamamResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para interactuar con la API de Edamam (información nutricional).
 */
interface IEdamanApiService {

    /**
     * Busca información nutricional de un ingrediente.
     *
     * @param appId ID de la aplicación (credencial).
     * @param appKey Clave de la aplicación (credencial).
     * @param ingredient Nombre del ingrediente a buscar.
     * @return Respuesta con datos nutricionales del ingrediente.
     */
    @GET("api/food-database/v2/parser")
    suspend fun searchFood(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") ingredient: String
    ): EdamamResponse
}
