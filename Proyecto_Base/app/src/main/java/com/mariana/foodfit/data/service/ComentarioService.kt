package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mariana.foodfit.data.entity.Comentario
import kotlinx.coroutines.tasks.await
import java.util.Date

class ComentarioService {

    private val db = FirebaseFirestore.getInstance()
    private val platillosCollection = db.collection("platillos")

    suspend fun getComentarios(platilloId: String): List<Comentario> {
        return try {
            platillosCollection.document(platilloId)
                .collection("comentarios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Comentario::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener comentarios: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPrimerosComentarios(platilloId: String, limite: Long = 3): List<Comentario> {
        return try {
            platillosCollection.document(platilloId)
                .collection("comentarios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(limite)
                .get()
                .await()
                .toObjects(Comentario::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener primeros comentarios: ${e.message}")
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

}