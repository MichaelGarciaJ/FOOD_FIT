package com.mariana.foodfit

import android.app.Application
import com.mariana.foodfit.utils.ThemeUtils

/**
 * Clase de aplicación principal que se ejecuta al iniciar la app.
 * Aquí se inicializan configuraciones globales.
 */
class FoodFitApp: Application() {

    /**
     * Método llamado cuando se crea la aplicación.
     * Aplica el tema (claro/oscuro/sistema) guardado en las preferencias.
     */
    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applySavedTheme(this)
    }

}