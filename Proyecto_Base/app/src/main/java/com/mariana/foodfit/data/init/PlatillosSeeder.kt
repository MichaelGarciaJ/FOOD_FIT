package com.mariana.foodfit.data.init

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mariana.foodfit.data.api.service.ApertiumApiService
import com.mariana.foodfit.data.api.service.EdamanApiService
import com.mariana.foodfit.data.api.service.MercadonaApiService
import com.mariana.foodfit.data.api.service.TheMealApiService
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.service.PlatilloService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PlatillosSeeder(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val platillioService: PlatilloService = PlatilloService(),
    private val theMealApiService: TheMealApiService = TheMealApiService(),
    private val mercadonaApiService: MercadonaApiService = MercadonaApiService(),
    private val edamanApiService: EdamanApiService = EdamanApiService(),
    private val apertiumApiService: ApertiumApiService = ApertiumApiService(),

    ) {

    suspend fun crearPlatilloCompleto(
        nombrePlatiilo: String,
        nombreIngredientes: Array<String>,
        preparacion: Map<Number, String>
    ) = withContext(Dispatchers.IO) {
        val collection = db.collection("platillos")
        val snapshot = collection.limit(1).get().await()


        // ESTÁ PARTE SERIA EN EL MAIN EN DONDE COMPRUEBA.
        if (!snapshot.isEmpty) {
            Log.d("PlatillosSeeder", "La colección 'platillos' ya tiene datos. No se hace nada.")
            return@withContext
        }

        Log.d(
            "PlatillosSeeder",
            "La colección 'platillos' está vacía. Se agregarán platillos iniciales."
        )

        val ingredientesList = mutableListOf<Ingrediente>()


        // Si exite el ingrediente en firestore, lo extaemos de ahí y nos ahorramos de buscar en las api.

        // Si no existe el ingrediente en firestore, lo buscamos en la api para luego guardarlo.

            // Obtener los ingredientes por nombre con MERCADONA

            // TRADUCCION (APERTIUM) del nombre del ingrediente

            // NUTRIENTES (EDAMAN) buscando con el nombre traducido.

            // Guardar en firestore ese ingrediente


        // TRADUCCION (APERTIUM) del nombre del platillo

        // FOTO (THEMEAL) buscando con el nombre del platillo traducido

        // Guardar ahora el platillo en firestore.

    }

}