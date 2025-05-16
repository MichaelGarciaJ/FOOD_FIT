package com.mariana.foodfit.ui.meals.createFoodPlate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.PlatilloVistaItem
import com.mariana.foodfit.data.service.PlatilloFavoritoService
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityMyFoodPlateBinding
import com.mariana.foodfit.ui.adapters.platilloView.PlatilloVistaAdapter
import com.mariana.foodfit.ui.search.SearchDialog
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

/**
 * Activity principal que permite al usuario visualizar, buscar, crear y eliminar sus platillos personalizados.
 * También ofrece la opción de marcar platillos como favoritos.
 */
class MyFoodPlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyFoodPlateBinding
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

        binding = ActivityMyFoodPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos el botón de menú para abrir/cerrar el drawer
        ToolbarUtils.configurarDrawerToggle(
            binding.myFoodPlateCustomToolbar,
            binding.myFoodPlateDrawerLayout
        )

        // Configuración de búsqueda
        ToolbarUtils.configurarBusqueda(binding.myFoodPlateCustomToolbar) {
            abrirDialogoBusqueda()
        }

        // Configurar el SwipeRefreshLayout
        configurarSwipeRefresh()

        recyclerView = binding.myFoodPlateRecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        // Inicializar adaptador con acción de favorito
        platilloAdapter = PlatilloVistaAdapter { onFavoriteClick(it) }
        recyclerView.adapter = platilloAdapter

        // Configurar botón eliminar platillo
        binding.myFoodPlateBtnEditFood.setOnClickListener {
            mostrarDialogoConfirmacionEliminar()
        }

        cargarTodoPlatillosFirestore()
        goToCreateFood()
    }

    /**
     * Método de ciclo de vida llamado cuando la actividad se reanuda.
     */
    override fun onResume() {
        super.onResume()
        cargarMisPlatillosFirestore()
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
     * Método que abre la pantalla para crear un nuevo platillo y finaliza la actividad actual.
     */
    private fun goToCreateFood() {
        binding.myFoodPlateBtnCreateFood.setOnClickListener {
            binding.myFoodPlateBtnCreateFood.isEnabled = false
            val intent = Intent(this@MyFoodPlateActivity, CreateFoodPlateActivity::class.java)
            startActivity(intent)
            finish()
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
     * Método que carga todos los platillos disponibles (sin filtrar por categoría y creados por "Sistema" o el usuario actual)
     * y almacena los ingredientes por platillo para búsquedas futuras.
     */
    private fun cargarTodoPlatillosFirestore() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillos()
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

            // Filtrar solo los platillos creados por el sistema o por el usuario actual
            val filtrados = platillos.filter {
                it.creadoPor == "Sistema" || it.creadoPor == userId
            }

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

            // Mostrar los platillos al usuario
            listaPlatillos = todosPlatillos.toMutableList()
            platilloAdapter.submitList(listaPlatillos.toList())
        }
    }

    /**
     * Método que carga exclusivamente los platillos creados por el usuario actual.
     */
    private fun cargarMisPlatillosFirestore() {
        binding.myFoodPlateSwipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val platillos = platilloService.getPlatillos()
            val favoritosIds = platilloFavoritoService.getFavoritosIds(userId)

            val misPlatillos = platillos.filter { it.creadoPor == userId }

            listaPlatillos = misPlatillos.map {
                ingredientesPorPlatillo[it.idPlatillo] = it.ingredientes.map { ingr -> ingr.nombre }

                PlatilloVistaItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl,
                    title = it.nombre,
                    subtitle = it.categoria,
                    isFavorite = favoritosIds.contains(it.idPlatillo)
                )
            }.toMutableList()

            platilloAdapter.submitList(listaPlatillos.toList())
            binding.myFoodPlateSwipeRefreshLayout.isRefreshing = false
        }
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
     * Método que muestra un cuadro de diálogo para confirmar la eliminación de un platillo, solicitando su nombre exacto.
     */
    private fun mostrarDialogoConfirmacionEliminar() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_eliminar_platillo, null)
        val input =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.editTextNombrePlatillo)
        val inputLayout =
            dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayoutNombrePlatillo)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar platillo")
            .setMessage("Escribe el nombre exacto del platillo para confirmar la eliminación:")
            .setView(dialogView)
            .setPositiveButton("Eliminar", null)
            .setNegativeButton("Cancelar") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()

        dialog.setOnShowListener {
            val buttonEliminar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            buttonEliminar.setOnClickListener {
                val nombreIngresado = input.text.toString().trim()
                if (nombreIngresado.isEmpty()) {
                    inputLayout.error = "El nombre no puede estar vacío"
                } else {
                    inputLayout.error = null
                    eliminarPlatilloPorNombre(nombreIngresado)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    /**
     * Método que elimina un platillo que coincida exactamente con el nombre ingresado por el usuario.
     */
    private fun eliminarPlatilloPorNombre(nombre: String) {
        val platilloAEliminar = listaPlatillos.find { it.title.equals(nombre, ignoreCase = true) }

        if (platilloAEliminar == null) {
            Utils.mostrarMensaje(
                this@MyFoodPlateActivity,
                "No se encontró ningún de tus platillos con ese nombre."
            )
            return
        }

        lifecycleScope.launch {
            val resultado = platilloService.eliminarPlatillo(platilloAEliminar.id)
            if (resultado) {
                Utils.mostrarMensaje(this@MyFoodPlateActivity, "Platillo eliminado correctamente.")
                cargarMisPlatillosFirestore()
            } else {
                Utils.mostrarMensaje(this@MyFoodPlateActivity, "Error al eliminar el platillo.")
            }
        }
    }

    /**
     * Método que configura el comportamiento del SwipeRefreshLayout, para refrescar la lista al hacer swipe.
     */
    private fun configurarSwipeRefresh() {
        binding.myFoodPlateSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )

        binding.myFoodPlateSwipeRefreshLayout.setOnRefreshListener {
            cargarMisPlatillosFirestore()
        }
    }

}