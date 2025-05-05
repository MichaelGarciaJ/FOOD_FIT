package com.mariana.foodfit.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.FoodItem
import com.mariana.foodfit.databinding.ActivityHomeBinding
import com.mariana.foodfit.ui.adapters.FoodAdapter
import com.mariana.foodfit.utils.ToolbarUtils

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos el botón de menú para abrir/cerrar el drawer
        ToolbarUtils.configurarDrawerToggle(binding.homeCustomToolbar, binding.homeDrawerLayout)

        val recyclerView = findViewById<RecyclerView>(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        val foodItems = listOf(
            FoodItem(R.drawable.logofoodfit_night, "Ensalada", "Fresca y saludable", false),
            FoodItem(R.drawable.logofoodfit_night, "Pasta", "Deliciosa al dente", true),
            FoodItem(R.drawable.logofoodfit_night, "Sopa", "Caliente y reconfortante", false),
            // Añade más elementos aquí
        )

        val adapter = FoodAdapter(foodItems)
        recyclerView.adapter = adapter

    }
}
