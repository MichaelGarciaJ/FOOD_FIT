package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mariana.foodfit.data.entity.Comentario
import kotlinx.coroutines.tasks.await

class ComentarioService {

    private val db = FirebaseFirestore.getInstance()
    private val platillosCollection = db.collection("platillos")

    suspend fun getComentarios(platilloId: String): List<Comentario> {
        return try {
            val snapshot = platillosCollection.document(platilloId)
                .collection("comentarios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { doc ->
                val comentario = doc.toObject(Comentario::class.java)
                comentario?.copy(id = doc.id) ?: Comentario()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener comentarios: ${e.message}")
            emptyList()
        }
    }


    suspend fun addComentario(
        platilloId: String,
        usuarioId: String,
        comentarioTexto: String,
        nombreUsuario: String,
        fotoUsuario: String
    ): String {
        val comentarioData = hashMapOf(
            "uid" to usuarioId,
            "texto" to comentarioTexto,
            "fecha" to FieldValue.serverTimestamp(),
            "nombreUsuario" to nombreUsuario,
            "fotoUsuario" to fotoUsuario,
            "platilloId" to platilloId
        )

        return try {
            val comentarioRef = platillosCollection.document(platilloId)
                .collection("comentarios")
                .add(comentarioData)
                .await()
            comentarioRef.id
        } catch (e: Exception) {
            Log.e("Firestore", "Error al agregar comentario: ${e.message}")
            ""
        }
    }

    suspend fun deleteComentario(platilloId: String, comentarioId: String): Boolean {
        return try {
            platillosCollection.document(platilloId)
                .collection("comentarios")
                .document(comentarioId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar comentario: ${e.message}")
            false
        }
    }

}