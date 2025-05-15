package com.mariana.foodfit.data.service

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mariana.foodfit.data.entity.Platillo
import kotlinx.coroutines.tasks.await

/**
 * Servicio que gestiona las operaciones relacionadas con los platillos.
 */
class PlatilloService {

    private val db = FirebaseFirestore.getInstance()
    private val platillosCollection = db.collection("platillos")
    private val usuariosCollection = db.collection("usuarios")

    /**
     * Método que recupera todos los platillos almacenados en la colección "platillos".
     *
     * @return Lista de objetos Platillo o una lista vacía si hay un error.
     */
    suspend fun getPlatillos(): List<Platillo> {
        return try {
            val platillos = platillosCollection.get().await() // Obtener todos los platillos
            platillos.toPlatillos()  // Convertir documentos a lista de platillos
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener platillos: ${e.message}")
            emptyList()  // Si hay error, devolver una lista vacía
        }
    }

    /**
     * Método que recupera los platillos que pertenecen a una categoría específica.
     *
     * @param categoria Categoría a filtrar.
     * @return Lista de platillos que coinciden con la categoría o una lista vacía.
     */
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

    /**
     * Método que recupera un platillo específico por su ID.
     *
     * @param id ID del platillo (document ID en Firestore).
     * @return Objeto Platillo correspondiente o null si no se encuentra o hay un error.
     */
    suspend fun getPlatilloById(id: String): Platillo? {
        return try {
            platillosCollection.document(id).get().await().toObject(Platillo::class.java)
                ?.copy(idPlatillo = id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Método que agrega un nuevo platillo a Firestore con un ID generado automáticamente.
     *
     * @param platillo Objeto Platillo que se desea agregar.
     * @return ID generado del nuevo platillo, o cadena vacía si falla.
     */
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

    /**
     * Método que convierte un QuerySnapshot en una lista de objetos Platillo.
     *
     * @return Lista de objetos Platillo válidos.
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