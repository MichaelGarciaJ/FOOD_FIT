package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.entity.Platillo
import com.mariana.foodfit.data.entity.PlatilloFavorito
import kotlinx.coroutines.tasks.await

class PlatilloFavoritoService {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")
    private val platillosCollection = db.collection("platillos")

    /**
     * Agrega o elimina un platillo de los favoritos de un usuario.
     */
    suspend fun toggleFavorito(userId: String, platilloId: String, isFavorite: Boolean) {
        val favoritosRef = usuariosCollection.document(userId)
            .collection("platillosFavoritos")
            .document(platilloId)

        try {
            if (isFavorite) {
                val favorito = PlatilloFavorito(
                    platilloId = platilloId,
                    userId = userId
                )
                favoritosRef.set(favorito).await()
            } else {
                favoritosRef.delete().await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al actualizar favorito: ${e.message}")
        }
    }

    /**
     * Obtiene los IDs de los platillos favoritos del usuario.
     */
    suspend fun getFavoritosIds(userId: String): Set<String> {
        return try {
            val snapshot = usuariosCollection.document(userId)
                .collection("platillosFavoritos")
                .get()
                .await()
            snapshot.documents.mapNotNull { it.id }.toSet()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener favoritos: ${e.message}")
            emptySet()
        }
    }


    /**
     * Devuelve la lista completa de platillos marcados como favoritos por el usuario.
     */
    suspend fun getPlatillosFavoritos(userId: String): List<Platillo> {
        return try {
            val snapshot = usuariosCollection.document(userId)
                .collection("platillosFavoritos")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                getPlatilloById(doc.id)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener platillos favoritos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Utiliza el ID para recuperar el platillo completo desde la colección principal.
     */
    private suspend fun getPlatilloById(id: String): Platillo? {
        return try {
            val doc = platillosCollection.document(id).get().await()
            doc.toObject(Platillo::class.java)?.copy(idPlatillo = doc.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener platillo $id: ${e.message}")
            null
        }
    }

}
