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
    private lateinit var platilloAdapter: PlatilloVistaAdapter
    private var listaPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()

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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillosPorCategoria("Comida")
            val favoritosIds = platilloService.getFavoritosIds(userId)

            listaPlatillos = platillos.map {
                PlatilloVistaItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl,
                    title = it.nombre,
                    subtitle = it.categoria,
                    isFavorite = favoritosIds.contains(it.idPlatillo)
                )
            }.toMutableList()

            platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
            recyclerView.adapter = platilloAdapter
            platilloAdapter.submitList(listaPlatillos.toList()) // Copia inmutable
        }
    }

    private fun onFavoriteClick(platilloVistaItem: PlatilloVistaItem) {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val nuevoEstado = !platilloVistaItem.isFavorite

            platilloService.toggleFavorito(
                userId = userId,
                platilloId = platilloVistaItem.id,
                isFavorite = nuevoEstado
            )

            // Actualiza localmente el favorito
            val index = listaPlatillos.indexOfFirst { it.id == platilloVistaItem.id }
            if (index != -1) {
                listaPlatillos[index] = listaPlatillos[index].copy(isFavorite = nuevoEstado)
                platilloAdapter.submitList(listaPlatillos.toList())
            }
        }
    }

}