package com.mariana.foodfit.ui.home

import FoodAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.FoodItem
import com.mariana.foodfit.data.service.ApiToFirestoreService
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.databinding.ActivityHomeBinding
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private val platilloService = PlatilloService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar menú lateral
        ToolbarUtils.configurarDrawerToggle(binding.homeCustomToolbar, binding.homeDrawerLayout)

        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Dos columnas

        // Cargar platillos al iniciar
        cargarPlatillosDesdeFirestore()

        // ⚠ SOLO PARA PRUEBAS → comenta esto cuando ya tengas datos cargados
         importarDatosDeApis()
    }

    private fun cargarPlatillosDesdeFirestore() {
        lifecycleScope.launch {
            val platillos = platilloService.getPlatillos()
            val adapter = FoodAdapter(platillos.map {
                FoodItem(
                    id = it.idPlatillo,
                    fotoUrl = it.fotoUrl,
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


    private fun onFavoriteClick(foodItem: FoodItem) {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            platilloService.toggleFavorito(
                userId = userId,
                platilloId = foodItem.id,
                isFavorite = !foodItem.isFavorite
            )
        }
    }

    private fun importarDatosDeApis() {
        val apiService = ApiToFirestoreService()
        CoroutineScope(Dispatchers.IO).launch {
            apiService.importarProductosMercadona()
        }
    }
}
