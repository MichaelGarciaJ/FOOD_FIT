package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.entity.Platillo
import com.mariana.foodfit.data.entity.PlatilloFavorito
import kotlinx.coroutines.tasks.await

/**
 * Servicio para gestionar los platillos favoritos de los usuarios en Firestore.
 */
class PlatilloFavoritoService {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")
    private val platillosCollection = db.collection("platillos")

    /**
     * Método que añade o elimina un platillo de la colección de favoritos de un usuario.
     *
     * @param userId ID del usuario.
     * @param platilloId ID del platillo a agregar o eliminar.
     * @param isFavorite true para agregar a favoritos, false para eliminar.
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
     * Método que obtiene los IDs de todos los platillos que el usuario ha marcado como favoritos.
     *
     * @param userId ID del usuario.
     * @return Conjunto de IDs de platillos favoritos.
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
     * Método que recupera la lista completa de objetos Platillo que el usuario ha marcado como favoritos.
     *
     * @param userId ID del usuario.
     * @return Lista de objetos Platillo favoritos.
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
     * Método que recupera un objeto Platillo completo a partir de su ID desde la colección principal.
     *
     * @param id ID del platillo.
     * @return Objeto Platillo o null si ocurre un error.
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
