package com.mariana.foodfit.ui.meals.lunch

import PlatilloVistaAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityLunchBinding
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.launch

class LunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLunchBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.lunchCustomToolbar, binding.lunchDrawerLayout)

        recyclerView = findViewById(R.id.lunchRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        cargarComidaPlatillosFirestore()
    }

    private fun cargarComidaPlatillosFirestore() {
        lifecycleScope.launch {
            val platillos = platilloService.getPlatillosPorCategoria("Comida")
            val adapter = PlatilloVistaAdapter(platillos.map {
                PlatilloVistaItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl,
                    title = it.nombre,
                    subtitle = it.categoria,
                    isFavorite = it.isFavorite
                )
            }) { foodItem ->
                onFavoriteClick(foodItem)
            }
            recyclerView.adapter = adapter
        }
    }

    private fun onFavoriteClick(platilloVistaItem: PlatilloVistaItem) {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            platilloService.toggleFavorito(
                userId = userId,
                platilloId = platilloVistaItem.id,
                isFavorite = !platilloVistaItem.isFavorite
            )
        }
    }
}