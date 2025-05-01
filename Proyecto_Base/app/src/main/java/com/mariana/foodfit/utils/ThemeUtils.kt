package com.mariana.foodfit.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Clase de utilidad para manejar el tema (claro, oscuro, seguir sistema) de la aplicación.
 * Guarda y recupera las preferencias del usuario usando SharedPreferences.
 */
object ThemeUtils {

    // Nombre del archivo de preferencias
    private const val PREFS_NAME = "settings"

    // Clave para guardar el modo de tema
    private const val KEY_THEME_MODE = "theme_mode"

    /**
     * Método que aplica el tema previamente guardado en las preferencias al iniciar la aplicación.
     *
     * @param context Contexto necesario para acceder a SharedPreferences.
     */
    fun applySavedTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedMode = prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedMode)
    }

    /**
     * Método que establece el modo de tema elegido por el usuario y lo guarda en las preferencias.
     *
     * @param context Contexto necesario para acceder a SharedPreferences.
     * @param mode Modo de tema (ej. MODE_NIGHT_NO, MODE_NIGHT_YES, MODE_NIGHT_FOLLOW_SYSTEM).
     */
    fun setThemeMode(context: Context, mode: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Método que obtiene el modo de tema actualmente guardado en las preferencias.
     *
     * @param context Contexto necesario para acceder a SharedPreferences.
     * @return El modo de tema guardado (por defecto sigue al sistema).
     */
    fun getCurrentMode(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

}