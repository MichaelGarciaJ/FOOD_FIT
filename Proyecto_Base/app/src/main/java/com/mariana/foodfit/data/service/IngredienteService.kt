package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.tasks.await

/**
 * Servicio que gestiona las operaciones relacionadas con los ingredientes.
 */
class IngredienteService {

    private val db = FirebaseFirestore.getInstance()
    private val ingredientesCollection = db.collection("ingredientes")

    /**
     * Método que recupera un ingrediente específico por su ID desde Firestore.
     *
     * @param id ID del ingrediente.
     * @return Objeto Ingrediente correspondiente o null si no se encuentra o hay un error.
     */
    suspend fun getIngredienteById(id: String): Ingrediente? {
        return try {
            ingredientesCollection.document(id).get().await()
                .toObject(Ingrediente::class.java)?.copy(idIngrediente = id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener ingrediente $id: ${e.message}")
            null
        }
    }

    /**
     * Método que recupera un ingrediente buscando por su nombre.
     *
     * @param nombre Nombre del ingrediente a buscar.
     * @return Ingrediente encontrado o null si no existe o si ocurre un error.
     */
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

    /**
     * Método que recupera todos los ingredientes almacenados en la colección "ingredientes".
     *
     * @return Lista de ingredientes o una lista vacía si ocurre un error.
     */
    suspend fun getAllIngredientes(): List<Ingrediente> {
        return try {
            val ingredientes =
                ingredientesCollection.get().await() // Obtener todos los ingredientes
            ingredientes.toIngredientes()
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener todos los ingredientes: ${e.message}")
            emptyList()
        }
    }

    /**
     * Método que agrega un nuevo ingrediente a Firestore.
     *
     * @param ingrediente Objeto Ingrediente a guardar.
     * @return ID generado del ingrediente guardado.
     * @throws Exception si ocurre un error al guardar.
     */
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

    /**
     * Método que convierte un QuerySnapshot en una lista de objetos Ingrediente.
     *
     * @return Lista de ingredientes válidos o vacía si ocurre un error de deserialización.
     */
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
