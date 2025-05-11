package com.mariana.foodfit

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.init.PlatillosInitialData
import com.mariana.foodfit.data.init.PlatillosSeeder
import com.mariana.foodfit.utils.ThemeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Clase de aplicación principal que se ejecuta al iniciar la app.
 * Aquí se inicializan configuraciones globales.
 */
class FoodFitApp : Application() {

    // Scope controlado, atado al ciclo de vida de la aplicación
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Método llamado cuando se crea la aplicación.
     * Aplica el tema visual guardado por el usuario y lanza la inicialización de datos.
     */
    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applySavedTheme(this)
        inicializarPlatillos()
    }

    /**
     * Método llamado cuando la app se termina (cerrada por completo).
     * Se cancelan las tareas en segundo plano para liberar recursos.
     */
    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }

    /**
     * Método que inicializa los platillos predeterminados si la base de datos está vacía.
     */
    private fun inicializarPlatillos() {
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("platillos")

        appScope.launch {
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