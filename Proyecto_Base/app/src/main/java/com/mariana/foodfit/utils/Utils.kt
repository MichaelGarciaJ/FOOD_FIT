package com.mariana.foodfit.utils

import android.content.Context
import android.widget.Toast

/**
 * Clase utils que contiene métodos auxiliares reutilizables para validaciones y operaciones comunes en la aplicación.
 *
 * Se implementa como un objeto Companion para permitir acceso directo
 * a los métodos sin necesidad de instanciar la clase.
 */
class Utils {
    companion object {

        /**
         * Método que valida si una cadena de texto cumple con el formato de un correo electrónico válido.
         *
         * @param email Cadena a validar como correo electrónico
         * @return true si el formato es válido, false en caso contrario
         */
        fun comprobarCorreo(email: String): Boolean {
            val emailRegex =
                "^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
            return email.matches(emailRegex)
        }

        /**
         * Método para validar el nombre de usuario.
         * Permite letras, números, guiones y guiones bajos.
         *
         * @param username El nombre de usuario que se quiere validar.
         * @return true si el nombre de usuario es válido, false si no cumple con las condiciones.
         */
        fun validarNombreUsuario(username: String): Boolean {
            val limpio = username.trim()
            val regex = Regex("^[a-zA-Z0-9 _-]+$")
            return limpio.length in 3..20 && regex.matches(limpio)
        }

        /**
         * Método que muestra un mensaje Toast corto en la pantalla.
         *
         * @param context Contexto de la aplicación/actividad
         * @param mensaje Texto a mostrar en el Toast (puede ser null)
         * @param duracion Tiempo que el mensaje va a estar en la pantalla (predeterminado Toast.LENGTH_SHORT)
         */
        fun mostrarMensaje(
            context: Context?,
            mensaje: String?,
            duracion: Int = Toast.LENGTH_SHORT
        ) {
            Toast.makeText(context, mensaje, duracion).show()
        }
    }

}