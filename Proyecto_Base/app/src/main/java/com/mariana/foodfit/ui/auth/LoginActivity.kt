package com.mariana.foodfit.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.ActivityLoginBinding


/**
 * Activity que muestra la pantalla de inicio de sesión (login).
 */
class LoginActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_login.xml
    private lateinit var binding: ActivityLoginBinding

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Navega hasta la vista activity_register.xml
        goToRegister()
    }

    override fun onResume() {
        super.onResume()
        // Volver a habilitar el clic cada vez que se muestra la pantalla
        binding.loginTvRegisterNow.isEnabled = true
    }

    /**
     * Método que configura el clic del TextView "¿No tienes cuenta? Regístrate ahora"
     * para navegar a la pantalla de registro (RegisterActivity).
     */
    private fun goToRegister() {
        binding.loginTvRegisterNow.setOnClickListener {
            binding.loginTvRegisterNow.isEnabled = false

            val intent = Intent(
                this@LoginActivity,
                RegisterActivity::class.java
            )
            startActivity(intent)

        }
    }


}