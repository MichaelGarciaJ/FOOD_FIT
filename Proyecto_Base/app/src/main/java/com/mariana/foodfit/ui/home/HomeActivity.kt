package com.mariana.foodfit.ui.home

import PlatilloVistaAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.api.service.MercadonaApiService
import com.mariana.foodfit.data.entity.PlatilloVistaItem
import com.mariana.foodfit.data.service.IngredienteService
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityHomeBinding
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()
    private val ingredienteService = IngredienteService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.homeCustomToolbar, binding.homeDrawerLayout)

        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

//        val testIngrediente = Ingrediente(
//            idIngrediente = "test123",
//            nombre = "Test Ingrediente",
//            precio = 1.0,
//            fotoUrl = "",
//            nutrientes = mapOf("calorias" to "100")
//        )
//
//        lifecycleScope.launch {
//            val id = ingredienteService.addIngrediente(testIngrediente)
//            Log.d("Firestore", "TEST Ingrediente guardado con ID: $id")
//        }

//        verificarYImportarDatosSiNecesario()

        // Desde donde llames el servicio:
        val mercadonaApiService = MercadonaApiService()

        lifecycleScope.launch {
            mercadonaApiService.buscarIngredientePorNombre("Aceite de oliva virgen extra Hacendado")
        }


    }

    private fun cargarPlatillosDesdeFirestore() {
        lifecycleScope.launch {
            val platillos = platilloService.getPlatillos()
            val adapter = PlatilloVistaAdapter(platillos.map {
                PlatilloVistaItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl ?: "",
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


//    private fun verificarYImportarDatosSiNecesario() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val ingredientesVacios = ingredienteService.getAllIngredientes().isEmpty()
//            val platillosVacios = platilloService.getPlatillos().isEmpty()
//
//            if (ingredientesVacios || platillosVacios) {
//                Log.d("HomeActivity", "Colecciones vacías, importando datos desde la API...")
//                val apiService = MercadonaApiService(
//                    ingredienteService = ingredienteService,
//                    platilloService = platilloService
//                )
//                apiService.importarProductosMercadona()
//                launch(Dispatchers.Main) {
//                    cargarPlatillosDesdeFirestore()
//                }
//            } else {
//                Log.d("HomeActivity", "Colecciones ya tienen datos, cargando desde Firestore...")
//                launch(Dispatchers.Main) {
//                    cargarPlatillosDesdeFirestore()
//                }
//            }
//        }
//    }


}
