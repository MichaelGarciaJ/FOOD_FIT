package com.mariana.foodfit.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mariana.foodfit.R

/**
 * Clase de utilidad para configurar Google Sign-In.
 */
object GoogleSignInHelper {

    /**
     * Método que devuelve un cliente configurado para iniciar sesión con Google.
     *
     * @param context Contexto necesario para acceder a los recursos de la aplicación.
     * @return GoogleSignInClient ya configurado con ID token y correo electrónico.
     */
    fun getClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}
