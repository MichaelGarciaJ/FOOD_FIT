package com.mariana.foodfit.data.service

import android.util.Log
import com.mariana.foodfit.data.api.ApiClient
import com.mariana.foodfit.data.api.model.ProductResponse
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MercadonaApiService(
    private val ingredienteService: IngredienteService,
    private val platilloService: PlatilloService
) {
    private val mercadonaApi = ApiClient.mercadonaApi

    suspend fun importarProductosMercadona() = withContext(Dispatchers.IO) {
        try {
            val rootCats = mercadonaApi.getCategories().results
            Log.d("Firestore", "Root categories: ${rootCats.size}")

            rootCats.take(10).forEach { root ->
                Log.d("Firestore", "Procesando categoría principal: ${root.name} (ID: ${root.id})")

                // Recorremos solo las subcategorías internas (las que tienen los productos)
                root.categories?.forEach { subCategory ->
                    Log.d("Firestore", "Procesando subcategoría: ${subCategory.name} (ID: ${subCategory.id})")

                    try {
                        val subDetail = mercadonaApi.getCategoryById(subCategory.id)

                        subDetail.products?.take(10)?.forEach { product ->
                            Log.d("Firestore", "Productos en subcategoría ${subCategory.name}: ${subDetail.products?.size ?: 0}")
                            saveProductToServices(product, "${root.name} → ${subCategory.name}")
                        }

                    } catch (e: Exception) {
                        Log.e("Firestore", "Error en subcategoría ${subCategory.name}", e)
                    }

                    delay(300) // Throttle para no saturar la API
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error general al importar productos", e)
        }
    }



    private suspend fun saveProductToServices(product: ProductResponse, categoryPath: String) {
        Log.d("Firestore", "Entra el método???")
        try {

            Log.d("Firestore", "Y AQUÍ Entra el método???")
            val price = product.price_instructions?.toDoubleOrNull() ?: 0.0
            Log.d("Firestore", "Producto: ${product.display_name}, price: $price")


            val ingrediente = Ingrediente(
                idIngrediente = product.id,
                nombre = product.display_name,
                precio = price,
                fotoUrl = product.thumbnail,
                nutrientes = mapOf(
                    "calorias" to "0",
                    "proteinas" to "0",
                    "carbohidratos" to "0",
                    "grasas" to "0"
                )
            )

            val ingredienteId = ingredienteService.addIngrediente(ingrediente)
            Log.d("Firestore", "Ingrediente guardado con ID: $ingredienteId")

            if (ingredienteId.isNotEmpty()) {
                val platillo = Platillo(
                    idPlatillo = product.id,
                    nombre = product.display_name,
                    categoria = categoryPath,
                    ingredientes = listOf(ingredienteId),
                    pasosPreparacion = listOf("Preparar ingrediente", "Cocinar al gusto"),
                    nutricion = mapOf(
                        "calorias" to 0.0,
                        "proteinas" to 0.0,
                        "carbohidratos" to 0.0,
                        "grasas" to 0.0
                    ),
                    fotoUrl = product.thumbnail,
                    creadoPor = "sistema"
                )

                platilloService.addPlatillo(platillo)
                Log.d("Firestore", "Platillo guardado: ${platillo.nombre}")
            } else {
                Log.e("Firestore", "Error: ingrediente no guardado, se omite platillo")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error en saveProductToServices", e)
        }
    }
}
