package com.mariana.foodfit.ui.favorite

import com.mariana.foodfit.ui.adapters.platilloView.PlatilloVistaAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.PlatilloFavoritoService
import com.mariana.foodfit.ui.meals.model.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityFavoriteBinding
import com.mariana.foodfit.ui.search.SearchDialog
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()
    private val platilloFavoritoService = PlatilloFavoritoService()
    private lateinit var platilloAdapter: PlatilloVistaAdapter
    private var listaPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()
    private val ingredientesPorPlatillo = mutableMapOf<String, List<String>>()
    private var todosPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.favoriteCustomToolbar, binding.favoriteDrawerLayout)

        // Configuración de búsqueda
        ToolbarUtils.configurarBusqueda(binding.favoriteCustomToolbar) {
            abrirDialogoBusqueda()
        }

        // Configurar el SwipeRefreshLayout
        configurarSwipeRefresh()

        recyclerView = findViewById(R.id.favoriteRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        // Crear adaptador una vez
        platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
        recyclerView.adapter = platilloAdapter

        cargarTodoPlatillosFirestore()
    }

    override fun onResume() {
        super.onResume()
        cargarFavoritoPlatillosFirestore()
    }

    private fun cargarTodoPlatillosFirestore() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillos()
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

            todosPlatillos = platillos.map {
                ingredientesPorPlatillo[it.idPlatillo] = it.ingredientes.map { ingr -> ingr.nombre }

                PlatilloVistaItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl,
                    title = it.nombre,
                    subtitle = it.categoria,
                    isFavorite = favoritosIds.contains(it.idPlatillo)
                )
            }.toMutableList()
        }
    }

    private fun cargarFavoritoPlatillosFirestore() {
        binding.favoriteSwipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloFavoritoService.getPlatillosFavoritos(userId)
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

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
            binding.favoriteSwipeRefreshLayout.isRefreshing = false

        }
    }

    private fun onFavoriteClick(platilloVistaItem: PlatilloVistaItem) {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val nuevoEstado = !platilloVistaItem.isFavorite

            platilloFavoritoService.toggleFavorito(
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

    private fun abrirDialogoBusqueda() {
        SearchDialog { query ->
            buscarPlatillos(query)
        }.show(supportFragmentManager, "SearchDialog")
    }

    private fun buscarPlatillos(query: String) {
        val resultados = todosPlatillos.filter { platillo ->
            val ingredientes = ingredientesPorPlatillo[platillo.id] ?: emptyList()
            ingredientes.any { it.contains(query, ignoreCase = true) }
        }

        if (resultados.isNotEmpty()) {
            platilloAdapter.submitList(resultados)
        } else {
            Utils.mostrarMensaje(this, "No se encontraron platillos con ese ingrediente.")
            platilloAdapter.submitList(listaPlatillos.toList()) // mostrar solo desayunos
        }
    }

    private fun configurarSwipeRefresh() {
        binding.favoriteSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )

        binding.favoriteSwipeRefreshLayout.setOnRefreshListener {
            cargarFavoritoPlatillosFirestore()
        }
    }

}