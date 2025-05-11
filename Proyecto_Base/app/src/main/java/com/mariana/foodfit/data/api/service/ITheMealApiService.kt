package com.mariana.foodfit.data.api.service

import com.mariana.foodfit.data.api.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para interactuar con la API de TheMealDB.
 */
interface ITheMealApiService {

    /**
     * Busca un platillo por su nombre.
     *
     * @param name Nombre del platillo a buscar.
     * @return Respuesta con la lista de platillos encontrados (puede ser null).
     */
    @GET("search.php")
    suspend fun searchMealByName(
        @Query("s") name: String
    ): MealResponse
}
