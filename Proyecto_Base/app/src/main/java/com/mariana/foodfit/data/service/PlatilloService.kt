package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.tasks.await

class PlatilloService {

    private val db = FirebaseFirestore.getInstance()
    private val platillosCollection = db.collection("platillos")
    private val usuariosCollection = db.collection("usuarios")

    suspend fun getPlatillos(): List<Platillo> {
        return try {
            val platillos = platillosCollection.get().await() // Obtener todos los platillos
            platillos.toPlatillos()  // Convertir documentos a lista de platillos
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener platillos: ${e.message}")
            emptyList()  // Si hay error, devolver una lista vacía
        }
    }


    suspend fun getPlatillosPorCategoria(categoria: String): List<Platillo> {
        return try {
            platillosCollection
                .whereEqualTo("categoria", categoria)
                .get().await()
                .toPlatillos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPlatilloById(id: String): Platillo? {
        return try {
            platillosCollection.document(id).get().await().toObject(Platillo::class.java)?.copy(idPlatillo = id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addPlatillo(platillo: Platillo): String {
        return try {
            val docRef = platillosCollection.document() // genera un nuevo ID
            val platilloWithId = platillo.copy(idPlatillo = docRef.id)
            docRef.set(platilloWithId).await()
            docRef.id // Devuelve el ID generado en Firestore
        } catch (e: Exception) {
            Log.e("Firestore", "Error al agregar platillo: ${e.message}")
            ""
        }
    }

    suspend fun toggleFavorito(userId: String, platilloId: String, isFavorite: Boolean) {
        val userRef = usuariosCollection.document(userId)
        val favoritosRef = userRef.collection("platillosFavoritos").document(platilloId)

        if (isFavorite) {
            favoritosRef.set(mapOf("platilloId" to platilloId, "timestamp" to System.currentTimeMillis())).await()
        } else {
            favoritosRef.delete().await()
        }
    }

    suspend fun getPlatillosFavoritos(userId: String): List<Platillo> {
        return try {
            usuariosCollection.document(userId)
                .collection("platillosFavoritos")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    getPlatilloById(doc.id) // Reutiliza la función existente
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarPorIngrediente(ingrediente: String): List<Platillo> {
        return try {
            platillosCollection
                .whereArrayContains("ingredientes", ingrediente)
                .get()
                .await()
                .toPlatillos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Extensión para convertir QuerySnapshot a List<Platillo>
     */
    private fun QuerySnapshot.toPlatillos(): List<Platillo> {
        return this.documents.mapNotNull { doc ->
            try {
                // Deserializa el platillo completo
                val platillo = doc.toObject(Platillo::class.java)
                // Aquí puedes agregar lógica adicional de validación si lo deseas
                platillo?.copy(idPlatillo = doc.id)
            } catch (e: Exception) {
                Log.e("Firestore", "Error mapeando documento ${doc.id}", e)
                null
            }
        }
    }

}