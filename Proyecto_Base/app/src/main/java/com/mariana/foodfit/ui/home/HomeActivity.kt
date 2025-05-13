package com.mariana.foodfit.ui.home

import com.mariana.foodfit.ui.adapters.platilloView.PlatilloVistaAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.ui.meals.model.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityHomeBinding
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()
    private lateinit var platilloAdapter: PlatilloVistaAdapter
    private var listaPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.homeCustomToolbar, binding.homeDrawerLayout)

        recyclerView = binding.homeRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        // Crear adaptador una vez
        platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
        recyclerView.adapter = platilloAdapter
    }

    override fun onResume() {
        super.onResume()
        cargarTodoPlatillosFirestore()
    }

    private fun cargarTodoPlatillosFirestore() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillos()
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

            // Usamos el adaptador ya existente, solo actualizamos la lista
            platilloAdapter.submitList(listaPlatillos.toList())
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

            // Actualizamos en la lista y reenviamos a DiffUtil
            val index = listaPlatillos.indexOfFirst { it.id == platilloVistaItem.id }
            if (index != -1) {
                listaPlatillos[index] = listaPlatillos[index].copy(isFavorite = nuevoEstado)
                platilloAdapter.submitList(listaPlatillos.toList()) // nueva copia → actualiza UI
            }
        }
    }

}
