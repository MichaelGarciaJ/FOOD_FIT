package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.api.ApiClient
import com.mariana.foodfit.data.api.model.CategoryResponse
import com.mariana.foodfit.data.api.model.ProductResponse
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ApiToFirestoreService {

    private val db                     = FirebaseFirestore.getInstance()
    private val platillosCollection    = db.collection("platillos")
    private val ingredientesCollection = db.collection("ingredientes")
    private val mercadonaApi           = ApiClient.mercadonaApi

    /** Punto de entrada */
    suspend fun importarProductosMercadona() = withContext(Dispatchers.IO) {
        try {
            val rootCats = mercadonaApi.getCategories().results
            Log.d("Firestore", "Root categories: ${rootCats.size}")

            // Procesar solo categorías principales que sabemos que funcionan
            rootCats.take(10).forEach { root ->
                Log.d("Firestore", "Procesando categoría principal: ${root.name} (ID: ${root.id})")

                try {
                    val detail = mercadonaApi.getCategoryDetail(root.id)

                    // Procesar solo productos directos de la categoría principal
                    detail.products?.take(20)?.forEach { product ->
                        saveProduct(product, root.name)
                    }

                    // Opcional: procesar primeras subcategorías si existen
                    detail.categories?.take(3)?.forEach { subCategory ->
                        try {
                            val subDetail = mercadonaApi.getCategoryDetail(subCategory.id)
                            subDetail.products?.take(10)?.forEach { product ->
                                saveProduct(product, "${root.name} → ${subCategory.name}")
                            }
                        } catch (e: Exception) {
                            Log.e("Firestore", "Error en subcategoría ${subCategory.name}", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Firestore", "Error en categoría ${root.name}", e)
                }

                delay(500) // Throttle para no saturar
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error general", e)
        }
    }

    /**
     * Baja recursivamente hasta que no hay más `categories`.
     * Cuando encuentra `products`, los guarda.
     */
    private suspend fun processCategory(cat: CategoryResponse, parentPath: String) {
        // pequeño throttle
        delay(300)

        val path = "$parentPath → ${cat.name}"
        val detail = try {
            Log.d("Firestore", "Llamando a detalle para: $path (id: ${cat.id})")
            mercadonaApi.getCategoryById(cat.id)
        } catch (ex: HttpException) {
            if (ex.code() == 404) {
                Log.w("Firestore", "404 (sin detalle) en categoría '$path'")
            } else {
                Log.e("Firestore", "HTTP ${ex.code()} en categoría '$path': ${ex.message()}")
            }
            return
        } catch (ex: Exception) {
            Log.e("Firestore", "Error al obtener categoría '$path': ${ex.message}")
            return
        }

        // si aún tiene sub‑categorías, seguimos bajando
        if (!detail.categories.isNullOrEmpty()) {
            detail.categories
                .take(10)    // opcional: límite de sub‑niveles
                .forEach { sub ->
                    processCategory(sub, parentPath = path)
                }
        }

        // si tiene productos hoja, los guardamos
        val prods = detail.products ?: emptyList()
        Log.d("Firestore", "Productos en $path: ${prods.size}")
        prods
            .take(20)
            .forEach { saveProduct(it, categoryPath = path) }
    }

    /** Guarda ingrediente + platillo en Firestore */
    private fun saveProduct(product: ProductResponse, categoryPath: String) {
        try {
            val ingrediente = Ingrediente(
                id = product.id,
                nombre = product.name,
                precio = product.price,
                fotoUrl = product.image,
                nutrientes = mapOf(
                    "calorias" to (product.nutrition?.kcal?.toString() ?: "0"),
                    "proteinas" to (product.nutrition?.proteins?.toString() ?: "0")
                )
            )

            // Guardar ingrediente
            ingredientesCollection.document(ingrediente.id)
                .set(ingrediente)
                .addOnSuccessListener {
                    Log.d("Firestore", "Ingrediente guardado: ${ingrediente.nombre}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error guardando ingrediente", e)
                }

            val platillo = Platillo(
                idPlatillo = product.id,
                nombre = product.name,
                categoria = categoryPath,
                ingredientes = listOf(ingrediente.id),
                pasosPreparacion = listOf("Preparar ingrediente", "Cocinar al gusto"),
                nutricion = mapOf(
                    "calorias" to (product.nutrition?.kcal ?: 0.0),
                    "proteinas" to (product.nutrition?.proteins ?: 0.0)
                ),
                fotoUrl = product.image,
                creadoPor = "sistema"
            )

            // Guardar platillo
            platillosCollection.document(platillo.idPlatillo)
                .set(platillo)
                .addOnSuccessListener {
                    Log.d("Firestore", "Platillo guardado: ${platillo.nombre}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error guardando platillo", e)
                }
        } catch (e: Exception) {
            Log.e("Firestore", "Error en saveProduct", e)
        }
    }

}
