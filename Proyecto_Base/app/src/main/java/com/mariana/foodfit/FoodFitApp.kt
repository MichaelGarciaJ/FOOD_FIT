package com.mariana.foodfit

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.init.PlatillosInitialData
import com.mariana.foodfit.data.init.PlatillosSeeder
import com.mariana.foodfit.utils.ThemeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Clase de aplicación principal que se ejecuta al iniciar la app.
 * Aquí se inicializan configuraciones globales.
 */
class FoodFitApp : Application() {

    /**
     * Método llamado cuando se crea la aplicación.
     * Aplica el tema (claro/oscuro/sistema) guardado en las preferencias.
     */
    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applySavedTheme(this)

        inicializarPlatillos()
    }

    private fun inicializarPlatillos() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("platillos")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = collection.limit(1).get().await()
                if (!snapshot.isEmpty) {
                    Log.d("FoodFitApp", "La colección 'platillos' ya tiene datos. No se hace nada.")
                    return@launch
                }

                Log.d(
                    "FoodFitApp",
                    "La colección 'platillos' está vacía. Se agregarán platillos iniciales."
                )

                val seeder = PlatillosSeeder()
                val platillos = PlatillosInitialData.obtenerPlatillosIniciales()

                for (platillo in platillos) {
                    seeder.crearPlatilloCompleto(
                        nombrePlatillo = platillo.nombre,
                        categoria = platillo.categoria,
                        ingredientesPlatillo = platillo.ingredientes,
                        pasosPreparacion = platillo.pasos,
                        isFavorito = false,
                        creadoPor = "sistema"
                    )
                }

                Log.d("FoodFitApp", "Se han agregado ${platillos.size} platillos iniciales.")
            } catch (e: Exception) {
                Log.e("FoodFitApp", "Error al inicializar platillos: ${e.message}", e)
            }
        }
    }

}