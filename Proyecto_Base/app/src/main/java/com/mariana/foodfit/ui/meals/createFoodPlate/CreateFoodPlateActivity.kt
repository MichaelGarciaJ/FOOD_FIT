package com.mariana.foodfit.ui.meals.createFoodPlate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.init.IngredientePlatillo
import com.mariana.foodfit.data.init.PlatillosSeeder
import com.mariana.foodfit.databinding.ActivityCreateFoodPlateBinding
import com.mariana.foodfit.ui.adapters.ingredient.IngredientPlatilloAdapter
import com.mariana.foodfit.ui.adapters.preparation.PreparationStepAdapter
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

class CreateFoodPlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFoodPlateBinding
    private lateinit var recyclerView: RecyclerView

    private lateinit var ingredientePlatilloAdapter: IngredientPlatilloAdapter
    private val ingredientes = mutableListOf<IngredientePlatillo>()

    private lateinit var preparationStepAdapter: PreparationStepAdapter
    private val pasos = mutableListOf<String>()

    private val categories = listOf("Cena", "Desayuno", "Comida", "Snack")

    private val platillosSeeder = PlatillosSeeder()

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

        ToolbarUtils.configurarDrawerToggle(
            binding.createFoodPlateCustomToolbar,
            binding.createFoodPlateDrawerLayout
        )
        binding.createFoodPlateCustomToolbar.mostrarBusqueda(false)

        configurarSwipeRefresh()
        configurarRecyclerViewIngredientes()
        configurarRecyclerViewPasos()
        addIngredienteRecyclerView()
        addPasoRecyclerView()
        goToMyPlatillosActivityCancel()
        configurarBotonGuardarPlatillo()
    }

    override fun onResume() {
        super.onResume()
        limpiarCamposCompletos()
    }

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

    private fun configurarBotonGuardarPlatillo() {
        binding.createFoodPlateBtnSave.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            // Validar campos antes de guardar
            if (validarCamposPlatillo()) {
                // Bloquear botón para evitar múltiples clics
                binding.createFoodPlateBtnSave.isEnabled = false

                val nombrePlatillo = binding.createFoodPlateEtName.text.toString().trim()
                val categoria = binding.createFoodPlateEtCategory.text.toString().trim()
                val isFavorito = false  // Puedes agregar toggle para esto si quieres
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
                        finish() // Cerrar CreateFoodPlateActivity

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@CreateFoodPlateActivity,
                            "Error al guardar el platillo: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        // Volver a habilitar el botón para intentar de nuevo
                        binding.createFoodPlateBtnSave.isEnabled = true
                    }
                }
            }
        }
    }


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

    private fun cleanCamposIngredientes() {
        binding.createFoodPlateEtNameIngredient.text?.clear()
        binding.createFoodPlateEtPriceIngredient.text?.clear()
        binding.createFoodPlateIngredientQuantity.text?.clear()
        binding.createFoodPlateIngredientUnit.text?.clear()
    }

    private fun configurarSwipeRefresh() {
        binding.createFoodPlateSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )
        binding.createFoodPlateSwipeRefreshLayout.setOnRefreshListener {
            // Puedes agregar lógica aquí para refrescar datos si lo deseas
            binding.createFoodPlateSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun goToMyPlatillosActivityCancel() {
        binding.createFoodPlateBtnCancel.setOnClickListener {
            binding.createFoodPlateBtnCancel.isEnabled = false
            val intent = Intent(this@CreateFoodPlateActivity, MyFoodPlateActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
