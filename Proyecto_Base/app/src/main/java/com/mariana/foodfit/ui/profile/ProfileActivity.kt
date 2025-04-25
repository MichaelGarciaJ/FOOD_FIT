package com.mariana.foodfit.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityProfileBinding
import com.mariana.foodfit.ui.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val usuarioService = UsuarioService()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inflar el layout usando ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)

        // Aplicar el tema guardado
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Configurar cliente de Google Sign-In (por si hay que cerrar sesión con él)
        configureGoogleSignIn()

        binding.btnCambiarTema.setOnClickListener {
            val currentlyDark = prefs.getBoolean("dark_mode", false)
            val editor = prefs.edit()
            editor.putBoolean("dark_mode", !currentlyDark)
            editor.apply()

            AppCompatDelegate.setDefaultNightMode(
                if (!currentlyDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Botón para cerrar sesión
        binding.cerrar.setOnClickListener {
            // Cerrar sesión de Firebase (siempre)
            usuarioService.logout()

            // Cerrar sesión de Google solo si fue login por Google
            val isGoogleLogin = prefs.getBoolean("is_google_login", false)
            if (isGoogleLogin) {
                googleSignInClient.signOut()
            }

            // Limpiar el flag para evitar confusiones en próximos inicios
            prefs.edit().remove("is_google_login").apply()

            // Redirigir a LoginActivity
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            finish()

        }
    }

    /**
     * Configura el cliente de Google Sign-In
     * (es necesario incluso si no siempre se usa)
     */
    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}
