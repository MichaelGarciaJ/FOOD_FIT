package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.tasks.await

class IngredienteService {

    private val db = FirebaseFirestore.getInstance()
    private val ingredientesCollection = db.collection("ingredientes")

    suspend fun getIngredienteById(id: String): Ingrediente? {
        return try {
            ingredientesCollection.document(id).get().await()
                .toObject(Ingrediente::class.java)?.copy(idIngrediente = id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener ingrediente $id: ${e.message}")
            null
        }
    }

    suspend fun getIngredienteByNombre(nombre: String): Ingrediente? {
        return try {
            val snapshot = ingredientesCollection
                .whereEqualTo("nombre", nombre)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val doc = snapshot.documents.first()
                doc.toObject(Ingrediente::class.java)?.copy(idIngrediente = doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error al buscar ingrediente por nombre '$nombre': ${e.message}", e)
            null
        }
    }


    suspend fun getAllIngredientes(): List<Ingrediente> {
        return try {
            val ingredientes = ingredientesCollection.get().await() // Obtener todos los ingredientes
            ingredientes.toIngredientes()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener todos los ingredientes: ${e.message}")
            emptyList()
        }
    }

    suspend fun addIngrediente(ingrediente: Ingrediente): String {
        return try {
            val docRef = ingredientesCollection.document(ingrediente.idIngrediente)
            docRef.set(ingrediente).await()
            docRef.id
        } catch (e: Exception) {
            Log.e("Firestore", "Error al agregar ingrediente: ${e.message}", e)
            throw e
        }
    }


    private fun QuerySnapshot.toIngredientes(): List<Ingrediente> {
        return this.documents.mapNotNull { doc ->
            try {
                // Deserializa el platillo completo
                val ingrediente = doc.toObject(Ingrediente::class.java)
                // Aquí puedes agregar lógica adicional de validación si lo deseas
                ingrediente?.copy(idIngrediente = doc.id)
            } catch (e: Exception) {
                Log.e("Firestore", "Error mapeando documento ${doc.id}", e)
                null
            }
        }
    }


}
