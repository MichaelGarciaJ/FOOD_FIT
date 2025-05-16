package com.mariana.foodfit.ui.meals.snack

import com.mariana.foodfit.ui.adapters.platilloView.PlatilloVistaAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.PlatilloFavoritoService
import com.mariana.foodfit.data.model.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivitySnackBinding
import com.mariana.foodfit.ui.search.SearchDialog
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

class SnackActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()
    private val platilloFavoritoService = PlatilloFavoritoService()
    private lateinit var platilloAdapter: PlatilloVistaAdapter
    private var listaPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()
    private val ingredientesPorPlatillo = mutableMapOf<String, List<String>>()
    private var todosPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySnackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.snackCustomToolbar, binding.snackDrawerLayout)

        // Configuración de búsqueda
        ToolbarUtils.configurarBusqueda(binding.snackCustomToolbar) {
            abrirDialogoBusqueda()
        }

        // Configurar el SwipeRefreshLayout
        configurarSwipeRefresh()

        recyclerView = binding.snackRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        // Crear adaptador una vez
        platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
        recyclerView.adapter = platilloAdapter

        cargarTodoPlatillosFirestore()
    }

    override fun onResume() {
        super.onResume()
        cargarSnackPlatillosFirestore()
    }

    private fun cargarTodoPlatillosFirestore() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillos()
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

            // Filtrar solo platillos creados por "Sistema" o por el usuario actual
            val filtrados = platillos.filter { it.creadoPor == "Sistema" || it.creadoPor == userId }

            todosPlatillos = filtrados.map {
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

    private fun cargarSnackPlatillosFirestore() {
        binding.snackSwipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillosPorCategoria("Snack")
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

            // Filtrar solo platillos creados por "Sistema" o por el usuario actual
            val filtrados = platillos.filter { it.creadoPor == "Sistema" || it.creadoPor == userId }

            listaPlatillos = filtrados.map {
                ingredientesPorPlatillo[it.idPlatillo] = it.ingredientes.map { ingr -> ingr.nombre }

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
            binding.snackSwipeRefreshLayout.isRefreshing = false
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

            // Actualiza localmente el favorito
            val index = listaPlatillos.indexOfFirst { it.id == platilloVistaItem.id }
            if (index != -1) {
                listaPlatillos[index] = listaPlatillos[index].copy(isFavorite = nuevoEstado)
                platilloAdapter.submitList(listaPlatillos.toList())
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
        binding.snackSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )

        binding.snackSwipeRefreshLayout.setOnRefreshListener {
            cargarSnackPlatillosFirestore()
        }
    }

}