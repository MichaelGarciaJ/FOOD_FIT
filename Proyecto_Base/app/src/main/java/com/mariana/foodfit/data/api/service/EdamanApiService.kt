package com.mariana.foodfit.data.api.service

import android.util.Log
import com.mariana.foodfit.data.api.client.EdamanApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EdamanApiService(
    private val appId: String,
    private val appKey: String
) {
    private val api = EdamanApiClient.edamamApi

    suspend fun obtenerNutrientesMap(nombre: String): Map<String, String> = withContext(Dispatchers.IO) {
        val response = api.searchFood(appId, appKey, nombre)

        val nutrientes = response.parsed.firstOrNull()?.food?.nutrients
            ?: response.hints.firstOrNull()?.food?.nutrients

        val result = mapOf(
            "calorias" to nutrientes?.ENERC_KCAL.toDisplayString(),
            "proteinas" to nutrientes?.PROCNT.toDisplayString(),
            "grasas" to nutrientes?.FAT.toDisplayString(),
            "carbohidratos" to nutrientes?.CHOCDF.toDisplayString(),
            "fibra" to nutrientes?.FIBTG.toDisplayString()
        )

        Log.d("Edamam", "Nutrientes Map para '$nombre': $result")
        result
    }

    private fun Double?.toDisplayString(): String = this?.toString() ?: "-"


}