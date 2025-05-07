package com.mariana.foodfit.data.service

import android.util.Log
import com.mariana.foodfit.data.api.ApiClient
import com.mariana.foodfit.data.api.model.PriceProduct
import com.mariana.foodfit.data.api.model.ProductDetailResponse
import com.mariana.foodfit.data.entity.Ingrediente
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
            val categoriaPrincipal = mercadonaApi.getCategories().results
            Log.d("Firestore", "Root categories: ${categoriaPrincipal.size}")

            categoriaPrincipal.take(10).forEach { dentroCategoriaPrincipal ->
                Log.d("Firestore", "Procesando categoría principal")

                dentroCategoriaPrincipal.categories?.take(10)?.forEach { categoriaSecundaria ->

                    // Obtenemos el ID para navegar.
                    val idSubCategory = categoriaSecundaria.id
                    Log.d(
                        "Firestore",
                        "Procesando subcategoría (ID: ${idSubCategory}))"
                    )

                    try {
                        val dentroCategoriaSecundaria =
                            mercadonaApi.getCategoryById(idSubCategory).categories

                        dentroCategoriaSecundaria.take(10).forEach { dentroProductoPrincipal ->
                            Log.d("Firestore", "Procesando producto principal")

                            dentroProductoPrincipal.products?.take(10)
                                ?.forEach { productoPrincipal ->
                                    val id = productoPrincipal.id;
                                    val nombreProducto = productoPrincipal.display_name;
                                    val fotoProducto = productoPrincipal.thumbnail;
                                    val precioProducto =
                                        productoPrincipal.price_instructions.bulk_price

                                    Log.d(
                                        "Firestore",
                                        "ID: ${id} - NOMBRE: ${nombreProducto} - FOTO: ${fotoProducto} - PRECIO: ${precioProducto}"
                                    )
                                    saveProductToServices(productoPrincipal);

                                }
                        }


                    } catch (e: Exception) {
                        Log.e("Firestore", "Error al obtener la subcategoria: ${e}")
                    }
                    delay(300) // Throttle para no saturar la API
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error general al importar productos", e)
        }
    }


    private suspend fun saveProductToServices(product: ProductDetailResponse) {
        try {

            val ingrediente = Ingrediente(
                idIngrediente = product.id,
                nombre = product.display_name,
                precio = product.price_instructions.bulk_price,
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
        } catch (e: Exception) {
            Log.e("Firestore", "Error en saveProductToServices", e)
        }
    }
}
