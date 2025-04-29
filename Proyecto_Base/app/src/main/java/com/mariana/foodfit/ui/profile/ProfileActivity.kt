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
        // Leer preferencias y aplicar modo oscuro ANTES de inflar la vista
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflar el layout usando ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar cliente de Google Sign-In
        configureGoogleSignIn()

        // Botón para cambiar el tema (oscuro/claro)
        binding.btnCambiarTema.setOnClickListener {
            val currentlyDark = prefs.getBoolean("dark_mode", false)
            prefs.edit().putBoolean("dark_mode", !currentlyDark).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (!currentlyDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Botón para cerrar sesión
        binding.cerrar.setOnClickListener {
            usuarioService.logout()

            if (prefs.getBoolean("is_google_login", false)) {
                googleSignInClient.signOut()
            }

            prefs.edit().remove("is_google_login").apply()

            startActivity(Intent(this, LoginActivity::class.java))
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
