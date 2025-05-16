package com.mariana.foodfit.ui.meals.createFoodPlate

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.init.IngredientePlatillo
import com.mariana.foodfit.data.init.PlatillosSeeder
import com.mariana.foodfit.databinding.ActivityCreateFoodPlateBinding
import com.mariana.foodfit.ui.adapters.ingredient.IngredientPlatilloAdapter
import com.mariana.foodfit.ui.adapters.platilloPreparation.PreparationStepAdapter
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

/**
 * Activity para la creación de un nuevo platillo,
 */
class CreateFoodPlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFoodPlateBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var ingredientePlatilloAdapter: IngredientPlatilloAdapter
    private val ingredientes = mutableListOf<IngredientePlatillo>()

    private lateinit var preparationStepAdapter: PreparationStepAdapter
    private val pasos = mutableListOf<String>()

    private val categories = listOf("Cena", "Desayuno", "Comida", "Snack")

    private val platillosSeeder = PlatillosSeeder()

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateFoodPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar dropdown para categoría
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,  // Layout Android estándar
            categories
        )
        binding.createFoodPlateEtCategory.setAdapter(categoryAdapter)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(
            binding.createFoodPlateCustomToolbar,
            binding.createFoodPlateDrawerLayout
        )

        // Desactivar búsqueda
        binding.createFoodPlateCustomToolbar.mostrarBusqueda(false)

        configurarSwipeRefresh()
        configurarRecyclerViewIngredientes()
        configurarRecyclerViewPasos()
        addIngredienteRecyclerView()
        addPasoRecyclerView()
        goToMyPlatillosActivityCancel()
        configurarBotonGuardarPlatillo()
    }

    /**
     * Método de ciclo de vida llamado cuando la actividad se reanuda.
     */
    override fun onResume() {
        super.onResume()
        limpiarCamposCompletos()
    }

    /**
     * Método que limpia todos los campos de entrada y las listas de ingredientes y pasos,
     * también notifica a los adapters para actualizar la UI.
     */
    private fun limpiarCamposCompletos() {
        // Limpiar textos de platillo
        binding.createFoodPlateEtName.text?.clear()
        binding.createFoodPlateEtCategory.text?.clear()

        // Limpiar listas y notificar adapters
        ingredientes.clear()
        ingredientePlatilloAdapter.notifyDataSetChanged()

        pasos.clear()
        preparationStepAdapter.notifyDataSetChanged()

        // Limpiar campos de ingredientes (los inputs)
        cleanCamposIngredientes()

        // Limpiar campo de nuevo paso
        binding.createFoodPlateNewStep.text?.clear()

        // Rehabilitar botón guardar por si estaba deshabilitado
        binding.createFoodPlateBtnSave.isEnabled = true
    }

    /**
     * Método que configura el botón para guardar el platillo.
     * Valida los campos, bloquea el botón para evitar múltiples clics,
     * y realiza el guardado en segundo plano.
     */
    private fun configurarBotonGuardarPlatillo() {
        binding.createFoodPlateBtnSave.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            // Validar campos antes de guardar
            if (validarCamposPlatillo()) {
                binding.createFoodPlateBtnSave.isEnabled = false

                val nombrePlatillo = binding.createFoodPlateEtName.text.toString().trim()
                val categoria = binding.createFoodPlateEtCategory.text.toString().trim()
                val isFavorito = false
                val creadoPor = userId

                // Ejecutar guardado en corrutina para no bloquear UI
                kotlinx.coroutines.MainScope().launch {
                    try {
                        // Llamar a crear platillo completo
                        platillosSeeder.crearPlatilloCompleto(
                            nombrePlatillo = nombrePlatillo,
                            categoria = categoria,
                            ingredientesPlatillo = ingredientes.toList(),
                            pasosPreparacion = pasos.toList(),
                            isFavorito = isFavorito,
                            creadoPor = creadoPor,
                        )
                        Toast.makeText(
                            this@CreateFoodPlateActivity,
                            "Platillo guardado con éxito",
                            Toast.LENGTH_LONG
                        ).show()

                        // Ir a MyFoodPlateActivity
                        val intent =
                            Intent(this@CreateFoodPlateActivity, MyFoodPlateActivity::class.java)
                        startActivity(intent)
                        finish()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@CreateFoodPlateActivity,
                            "Error al guardar el platillo: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.createFoodPlateBtnSave.isEnabled = true
                    }
                }
            }
        }
    }

    /**
     * Método que configura RecyclerView para mostrar y gestionar la lista de ingredientes.
     * Permite eliminar ingredientes con un callback.
     */
    private fun configurarRecyclerViewIngredientes() {
        recyclerView = binding.createFoodPlateIngredientsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        ingredientePlatilloAdapter = IngredientPlatilloAdapter(ingredientes) { ingrediente ->
            val index = ingredientes.indexOf(ingrediente)
            if (index != -1) {
                ingredientes.removeAt(index)
                ingredientePlatilloAdapter.notifyItemRemoved(index)
            }
        }

        recyclerView.adapter = ingredientePlatilloAdapter
    }

    /**
     * Método que configura RecyclerView para mostrar y gestionar la lista de pasos de preparación.
     * Permite eliminar pasos con un callback y actualiza el adapter.
     */
    private fun configurarRecyclerViewPasos() {
        val recyclerViewPasos = binding.createFoodPlatePreparationRecyclerView
        recyclerViewPasos.layoutManager = LinearLayoutManager(this)

        preparationStepAdapter = PreparationStepAdapter(pasos) { paso ->
            val index = pasos.indexOf(paso)
            if (index != -1) {
                pasos.removeAt(index)
                preparationStepAdapter.notifyItemRemoved(index)
                preparationStepAdapter.notifyItemRangeChanged(index, pasos.size - index)
            }
        }

        recyclerViewPasos.adapter = preparationStepAdapter
    }

    /**
     * Método que configura el botón para agregar un nuevo ingrediente.
     * Valida campos, crea el objeto ingrediente y actualiza el adapter.
     */
    private fun addIngredienteRecyclerView() {
        binding.createFoodPlateBtnAddIngredient.setOnClickListener {
            if (validarCamposIngredientes()) {
                val nombre = binding.createFoodPlateEtNameIngredient.text.toString().trim()
                val precioStr = binding.createFoodPlateEtPriceIngredient.text.toString().trim()
                val cantidadStr = binding.createFoodPlateIngredientQuantity.text.toString().trim()
                val unidad = binding.createFoodPlateIngredientUnit.text.toString().trim()

                val cantidad = cantidadStr.toIntOrNull()
                val precio = precioStr.toDoubleOrNull()

                if (cantidad == null || precio == null) {
                    Utils.mostrarMensaje(this, "Cantidad o precio no son válidos")
                    return@setOnClickListener
                }

                val nuevoIngrediente = IngredientePlatillo(
                    nombre = nombre,
                    cantidad = cantidad,
                    unidad = unidad
                )

                ingredientes.add(nuevoIngrediente)
                ingredientePlatilloAdapter.notifyItemInserted(ingredientes.size - 1)

                cleanCamposIngredientes()
            }
        }
    }

    /**
     * Método que configura el botón para agregar un nuevo paso de preparación.
     * Valida que el campo no esté vacío antes de agregar y notificar al adapter.
     */
    private fun addPasoRecyclerView() {
        binding.createFoodPlateBtnAddStep.setOnClickListener {

            val textoPaso = binding.createFoodPlateNewStep.text.toString().trim()
            if (textoPaso.isNotEmpty()) {
                pasos.add(textoPaso)
                preparationStepAdapter.notifyItemInserted(pasos.size - 1)
                binding.createFoodPlateNewStep.text?.clear()
            } else {
                Utils.mostrarMensaje(this, "El campo nuevo paso no puede estar vacío")
            }
        }
    }

    /**
     * Método que valida que los campos principales del platillo (nombre, categoría,
     * ingredientes y pasos) no estén vacíos antes de guardar.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private fun validarCamposPlatillo(): Boolean {
        val nombrePlatillo = binding.createFoodPlateEtName.text.toString().trim()
        val categoria = binding.createFoodPlateEtCategory.text.toString().trim()

        if (nombrePlatillo.isEmpty()) {
            Utils.mostrarMensaje(this, "El nombre del platillo está vacío")
            return false
        }
        if (categoria.isEmpty()) {
            Utils.mostrarMensaje(this, "La categoría está vacía")
            return false
        }
        if (ingredientes.isEmpty()) {
            Utils.mostrarMensaje(this, "Agrega al menos un ingrediente")
            return false
        }
        if (pasos.isEmpty()) {
            Utils.mostrarMensaje(this, "Agrega al menos un paso de preparación")
            return false
        }
        return true
    }

    /**
     * Método que valida que los campos para agregar un ingrediente no estén vacíos.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private fun validarCamposIngredientes(): Boolean {
        val nombre = binding.createFoodPlateEtNameIngredient.text.toString().trim()
        val precio = binding.createFoodPlateEtPriceIngredient.text.toString().trim()
        val cantidad = binding.createFoodPlateIngredientQuantity.text.toString().trim()
        val unidad = binding.createFoodPlateIngredientUnit.text.toString().trim()

        if (nombre.isEmpty()) {
            Utils.mostrarMensaje(this, "El campo nombre ingrediente está vacío")
            return false
        }

        if (precio.isEmpty()) {
            Utils.mostrarMensaje(this, "El campo precio está vacío")
            return false
        }

        if (cantidad.isEmpty()) {
            Utils.mostrarMensaje(this, "El campo cantidad está vacío")
            return false
        }

        if (unidad.isEmpty()) {
            Utils.mostrarMensaje(this, "El campo unidad está vacío")
            return false
        }

        return true
    }

    /**
     * Método que limpia los campos de texto relacionados a la entrada de ingredientes.
     */
    private fun cleanCamposIngredientes() {
        binding.createFoodPlateEtNameIngredient.text?.clear()
        binding.createFoodPlateEtPriceIngredient.text?.clear()
        binding.createFoodPlateIngredientQuantity.text?.clear()
        binding.createFoodPlateIngredientUnit.text?.clear()
    }

    /**
     * Método que configura el SwipeRefreshLayout para permitir refrescar la pantalla.
     * Actualmente solo detiene la animación de refresco.
     */
    private fun configurarSwipeRefresh() {
        binding.createFoodPlateSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )
        binding.createFoodPlateSwipeRefreshLayout.setOnRefreshListener {
            binding.createFoodPlateSwipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * Método que configura el botón cancelar para regresar a la actividad de mis platillos
     * y finalizar esta actividad.
     */
    private fun goToMyPlatillosActivityCancel() {
        binding.createFoodPlateBtnCancel.setOnClickListener {
            binding.createFoodPlateBtnCancel.isEnabled = false
            val intent = Intent(this@CreateFoodPlateActivity, MyFoodPlateActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
