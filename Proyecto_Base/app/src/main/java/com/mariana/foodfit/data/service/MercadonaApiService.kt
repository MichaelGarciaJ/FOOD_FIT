package com.mariana.foodfit.data.service

import android.util.Log
import com.mariana.foodfit.data.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

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
                Log.d("Firestore", "Procesando categoría principal")

                root.categories?.take(10)?.forEach { subCategory ->
                    Log.d(
                        "Firestore",
                        "Dentro de subcategoría"
                    )

                    // Obtenemos el ID para navegar.
                    val idCategory = subCategory.id
                    Log.d(
                        "Firestore",
                        "Procesando subcategoría (ID: ${idCategory}))"
                    )


                    delay(300) // Throttle para no saturar la API
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error general al importar productos", e)
        }
    }


//    private suspend fun saveProductToServices(product: ProductResponse, categoryPath: String) {
//        Log.d("Firestore", "Entra el método???")
//        try {
//
//            Log.d("Firestore", "Y AQUÍ Entra el método???")
//            val price = product.price_instructions?.toDoubleOrNull() ?: 0.0
//            Log.d("Firestore", "Producto: ${product.display_name}, price: $price")
//
//
//            val ingrediente = Ingrediente(
//                idIngrediente = product.id,
//                nombre = product.display_name,
//                precio = price,
//                fotoUrl = product.thumbnail,
//                nutrientes = mapOf(
//                    "calorias" to "0",
//                    "proteinas" to "0",
//                    "carbohidratos" to "0",
//                    "grasas" to "0"
//                )
//            )
//
//            val ingredienteId = ingredienteService.addIngrediente(ingrediente)
//            Log.d("Firestore", "Ingrediente guardado con ID: $ingredienteId")

//            if (ingredienteId.isNotEmpty()) {
//                val platillo = Platillo(
//                    idPlatillo = product.id,
//                    nombre = product.display_name,
//                    categoria = categoryPath,
//                    ingredientes = listOf(ingredienteId),
//                    pasosPreparacion = listOf("Preparar ingrediente", "Cocinar al gusto"),
//                    nutricion = mapOf(
//                        "calorias" to 0.0,
//                        "proteinas" to 0.0,
//                        "carbohidratos" to 0.0,
//                        "grasas" to 0.0
//                    ),
//                    fotoUrl = product.thumbnail,
//                    creadoPor = "sistema"
//                )
//
//                platilloService.addPlatillo(platillo)
//                Log.d("Firestore", "Platillo guardado: ${platillo.nombre}")
//            } else {
//                Log.e("Firestore", "Error: ingrediente no guardado, se omite platillo")
//            }
//        } catch (e: Exception) {
//            Log.e("Firestore", "Error en saveProductToServices", e)
//        }
//    }
}
