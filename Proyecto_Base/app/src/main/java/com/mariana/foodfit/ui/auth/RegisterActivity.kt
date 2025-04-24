package com.mariana.foodfit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.ActivityLoginBinding
import com.mariana.foodfit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_login.xml
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Finaliza la vista actual y te lleva a la última vista abierta (activity_activity.xml).
        cancelAndGoToLogin();

    }

    private fun cancelAndGoToLogin() {
        binding.registerTvLoginNow.setOnClickListener {
            finish();
        }
    }


}