package com.mariana.foodfit.ui.meals.lunch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloFavoritoService
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityLunchBinding
import com.mariana.foodfit.ui.adapters.platilloView.PlatilloVistaAdapter
import com.mariana.foodfit.ui.search.SearchDialog
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

/**
 * Activity que muestra los platillos de categoría "Comida" disponibles para el usuario.
 */
class LunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLunchBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()
    private val platilloFavoritoService = PlatilloFavoritoService()
    private lateinit var platilloAdapter: PlatilloVistaAdapter
    private var listaPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()
    private val ingredientesPorPlatillo = mutableMapOf<String, List<String>>()
    private var todosPlatillos: MutableList<PlatilloVistaItem> = mutableListOf()

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.lunchCustomToolbar, binding.lunchDrawerLayout)

        // Configuración de búsqueda
        ToolbarUtils.configurarBusqueda(binding.lunchCustomToolbar) {
            abrirDialogoBusqueda()
        }

        // Configurar el SwipeRefreshLayout
        configurarSwipeRefresh()

        recyclerView = binding.lunchRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Inicializar adaptador con acción de favorito
        platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
        recyclerView.adapter = platilloAdapter

        cargarTodoPlatillosFirestore()
    }

    /**
     * Método de ciclo de vida llamado cuando la actividad se reanuda.
     */
    override fun onResume() {
        super.onResume()
        cargarComidaPlatillosFirestore()
    }

    /**
     * Método que carga todos los platillos disponibles (sin filtrar por categoría y creados por "Sistema" o el usuario actual)
     * y almacena los ingredientes por platillo para búsquedas futuras.
     */
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

    /**
     * Método que carga solo los platillos de categoría "Comida"
     * y actualiza la UI con los resultados.
     */
    private fun cargarComidaPlatillosFirestore() {
        binding.lunchSwipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillosPorCategoria("Comida")
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
            binding.lunchSwipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * Método que alterna el estado de favorito de un platillo (agrega o quita de favoritos).
     *
     * @param platilloVistaItem Platillo que fue marcado/desmarcado como favorito.
     */
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

    /**
     * Método que muestra un diálogo para que el usuario pueda buscar platillos por ingrediente.
     */
    private fun abrirDialogoBusqueda() {
        SearchDialog { query ->
            buscarPlatillos(query)
        }.show(supportFragmentManager, "SearchDialog")
    }

    /**
     * Método que filtra los platillos según un término de búsqueda que coincida
     * con los ingredientes.
     *
     * @param query Término de búsqueda ingresado por el usuario.
     */
    private fun buscarPlatillos(query: String) {
        val resultados = todosPlatillos.filter { platillo ->
            val ingredientes = ingredientesPorPlatillo[platillo.id] ?: emptyList()
            ingredientes.any { it.contains(query, ignoreCase = true) }
        }

        if (resultados.isNotEmpty()) {
            platilloAdapter.submitList(resultados)
        } else {
            Utils.mostrarMensaje(this, "No se encontraron platillos con ese ingrediente.")
            platilloAdapter.submitList(listaPlatillos.toList())
        }
    }

    /**
     * Método que configura el comportamiento del SwipeRefreshLayout, para refrescar la lista al hacer swipe.
     */
    private fun configurarSwipeRefresh() {
        binding.lunchSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )

        binding.lunchSwipeRefreshLayout.setOnRefreshListener {
            cargarComidaPlatillosFirestore()
        }
    }

}