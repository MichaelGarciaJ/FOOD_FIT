package com.mariana.foodfit.ui.meals.foodPlateInfo

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.Platillo
import com.mariana.foodfit.data.service.IngredienteService
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityFoodPlateInfoBinding
import com.mariana.foodfit.ui.adapters.ingredient.IngredientAdapter
import com.mariana.foodfit.ui.adapters.ingredientDetail.IngredientDetailAdapter
import com.mariana.foodfit.ui.adapters.platilloPreparation.PreparationAdapter
import com.mariana.foodfit.ui.meals.model.Ingredient
import com.mariana.foodfit.ui.meals.model.IngredientDetail
import com.mariana.foodfit.ui.meals.model.PlatilloVistaItem
import com.mariana.foodfit.ui.meals.model.PreparationStep
import com.mariana.foodfit.utils.PDFGenerator
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class FoodPlateInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodPlateInfoBinding
    private lateinit var recyclerViewIngredientes: RecyclerView
    private lateinit var recyclerViewDetailIngr: RecyclerView
    private lateinit var recyclerViewPreparacion: RecyclerView
    private var platilloVista: PlatilloVistaItem? = null
    private val platilloService = PlatilloService()
    private val ingredienteService = IngredienteService()

    // Instanciamos los adaptadores
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var ingredientDetailAdapter: IngredientDetailAdapter
    private lateinit var preparationAdapter: PreparationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoodPlateInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(
            binding.foodPlateInfoCustomToolbar,
            binding.foodPlateInfoDrawerLayout
        )

        // Configurar el SwipeRefreshLayout
        configurarSwipeRefresh()

        // Ocultar el icono búsqueda del toolbar
        binding.foodPlateInfoCustomToolbar.mostrarBusqueda(false)

        // Inicializamos los RecyclerView
        recyclerViewIngredientes = binding.foodPlateInfoIngredientsRv
        recyclerViewDetailIngr = binding.foodPlateInfoIngredientsRv2
        recyclerViewPreparacion = binding.foodPlateInfoIngredientsRv3

        recyclerViewIngredientes.layoutManager = LinearLayoutManager(this)
        recyclerViewDetailIngr.layoutManager = LinearLayoutManager(this)
        recyclerViewPreparacion.layoutManager = LinearLayoutManager(this)

        // Instanciamos los adaptadores
        ingredientAdapter = IngredientAdapter()
        ingredientDetailAdapter = IngredientDetailAdapter()
        preparationAdapter = PreparationAdapter()

        // Asignamos los adaptadores a los RecyclerViews
        recyclerViewIngredientes.adapter = ingredientAdapter
        recyclerViewDetailIngr.adapter = ingredientDetailAdapter
        recyclerViewPreparacion.adapter = preparationAdapter

        setupShareButton()
    }

    override fun onResume() {
        super.onResume()
        binding.foodPlateInfoShareButton.isEnabled = true

        refrescarContenido()
    }

    private fun loadPlatilloData(platilloId: String) {
        lifecycleScope.launch {
            try {
                val platillo = platilloService.getPlatilloById(platilloId)
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val favoritosIds = platilloService.getFavoritosIds(userId)

                if (platillo != null) {
                    // Mostrar datos
                    binding.foodPlateInfoFoodTitle.text = platillo.nombre
                    binding.foodPlateInfoCategory.text = platillo.categoria

                    Glide.with(this@FoodPlateInfoActivity)
                        .load(platillo.fotoUrl)
                        .placeholder(R.drawable.ic_food_no)
                        .error(R.drawable.ic_cancel)
                        .into(binding.foodPlateInfoIvIcon)

                    val isFavorite = favoritosIds.contains(platillo.idPlatillo)
                    platilloVista = PlatilloVistaItem(
                        id = platillo.idPlatillo,
                        fotoUrl = platillo.fotoUrl,
                        title = platillo.nombre,
                        subtitle = platillo.categoria,
                        isFavorite = isFavorite
                    )

                    updateFavoriteIcon(isFavorite)

                    // Set listener
                    binding.foodPlateInfoFoodFavoriteIcon.setOnClickListener {
                        onFavoriteClick()
                    }

                    loadIngredientes(platillo)
                }
            } catch (e: Exception) {
                Log.e("FoodPlateInfoActivity", "Error al cargar platillo: ${e.message}")
            }
        }
    }

    private fun loadIngredientes(platillo: Platillo) {
        lifecycleScope.launch {
            try {
                val ingredientesSimples = mutableListOf<Ingredient>()
                val detalles = mutableListOf<IngredientDetail>()
                val pasos = mutableListOf<PreparationStep>()

                val corutinas = platillo.ingredientes.map { ingredientePlatillo ->
                    async {
                        val nombreIngrediente = ingredientePlatillo.nombre
                        val cantidad = ingredientePlatillo.cantidad
                        val unidad = ingredientePlatillo.unidad

                        val ingrediente =
                            ingredienteService.getIngredienteByNombre(nombreIngrediente)
                        Log.d(
                            "FoodPlateInfoActivity",
                            "Ingrediente $nombreIngrediente: $ingrediente"
                        )

                        if (ingrediente != null) {
                            val precioString = String.format("%.2f", ingrediente.precio)
                            val caloriasString = String.format("%.2f", ingrediente.calorias)
                            val grasasString = String.format("%.2f", ingrediente.grasas)
                            val carbohidratosString =
                                String.format("%.2f", ingrediente.carbohidratos)
                            val fibraString = String.format("%.2f", ingrediente.fibra)
                            val proteinasString = String.format("%.2f", ingrediente.proteinas)

                            // Lista 1: Básica
                            ingredientesSimples.add(
                                Ingredient(
                                    nombre = nombreIngrediente,
                                    cantidad = cantidad,
                                    unidad = unidad,
                                    precio = convertToDouble(precioString)
                                )
                            )

                            // Lista 2: Detalle nutricional
                            detalles.add(
                                IngredientDetail(
                                    nombre = nombreIngrediente,
                                    calorias = convertToDouble(caloriasString),
                                    grasas = convertToDouble(grasasString),
                                    carbohidratos = convertToDouble(carbohidratosString),
                                    fibra = convertToDouble(fibraString),
                                    proteina = convertToDouble(proteinasString)
                                )
                            )
                        }
                    }
                }

                // Esperamos a que todas las corutinas terminen
                corutinas.awaitAll()

                // Lista 3: Preparación
                platillo.pasosPreparacion.forEachIndexed { index, paso ->
                    pasos.add(PreparationStep(numero = index + 1, texto = paso))
                }

                // Actualizamos los adaptadores con los resultados obtenidos
                ingredientAdapter.submitList(ingredientesSimples)
                ingredientDetailAdapter.submitList(detalles)
                preparationAdapter.submitList(pasos)

            } catch (e: Exception) {
                Log.e("FoodPlateInfoActivity", "Error al cargar ingredientes: ${e.message}")
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_favorite_background else R.drawable.ic_favorite
        binding.foodPlateInfoFoodFavoriteIcon.setImageResource(iconRes)
    }

    private fun onFavoriteClick() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val platillo = platilloVista ?: return

        val nuevoEstado = !platillo.isFavorite

        lifecycleScope.launch {
            platilloService.toggleFavorito(
                userId = userId,
                platilloId = platillo.id,
                isFavorite = nuevoEstado
            )

            platilloVista = platillo.copy(isFavorite = nuevoEstado)
            updateFavoriteIcon(nuevoEstado)
        }
    }

    private fun convertToDouble(value: String?): Double {
        return value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
    }

    private fun setupShareButton() {
        binding.foodPlateInfoShareButton.setOnClickListener {
            binding.foodPlateInfoShareButton.isEnabled = false
            platilloVista?.let { platillo ->
                // Crear el texto a compartir
                val shareText = """
                🍽 ${platillo.title}
                📂 Categoría: ${platillo.subtitle}
                
                ¡Descubre este platillo descargando en Food Fit!
            """.trimIndent()

                // Crear un cuadro de diálogo para elegir qué compartir
                val options = arrayOf("Mensaje", "PDF")
                val builder = android.app.AlertDialog.Builder(this)

                builder.setTitle("¿Qué deseas compartir?")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> {
                                shareTextOnly(shareText)
                            }
                            1 -> {
                                sharePdfWithText(shareText)
                            }
                        }
                    }
                    .show()
            }
        }
    }

    private fun shareTextOnly(shareText: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "Compartir platillo con:")
        startActivity(shareIntent)
    }

    private fun sharePdfWithText(shareText: String) {
        val pdfFile = PDFGenerator.generateRecipePDF(
            context = this,
            platilloVista = platilloVista,
            ingredients = ingredientAdapter.currentList,
            ingredientDetails = ingredientDetailAdapter.currentList,
            preparationSteps = preparationAdapter.currentList
        )

        if (pdfFile != null) {
            val pdfUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, pdfUri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "Información del platillo")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                clipData = android.content.ClipData.newUri(contentResolver, "PDF", pdfUri)
            }

            val chooser = Intent.createChooser(intent, "Compartir con:")
            startActivity(chooser)
        }
    }

    private fun refrescarContenido() {
        val platilloId = intent.getStringExtra("PLATILLO") ?: return
        binding.foodPlateInfoSwipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            loadPlatilloData(platilloId)
            binding.foodPlateInfoSwipeRefreshLayout.isRefreshing = false
        }
    }


    private fun configurarSwipeRefresh() {
        binding.foodPlateInfoSwipeRefreshLayout.setColorSchemeColors(
            getColor(R.color.md_theme_primary)
        )

        binding.foodPlateInfoSwipeRefreshLayout.setOnRefreshListener {
            refrescarContenido()
        }
    }


}
