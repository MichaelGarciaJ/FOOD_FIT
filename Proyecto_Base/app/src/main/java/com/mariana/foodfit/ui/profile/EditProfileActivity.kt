package com.mariana.foodfit.ui.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.ActivityEditProfileBinding
import com.mariana.foodfit.databinding.ActivityHomeBinding
import com.mariana.foodfit.utils.ToolbarUtils

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos el botón de menú para abrir/cerrar el drawer
        ToolbarUtils.configurarDrawerToggle(binding.editProfileCustomToolbar, binding.editProfileDrawerLayout)

    }
}