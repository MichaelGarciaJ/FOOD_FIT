package com.mariana.foodfit.data.init

import android.util.Log
import com.mariana.foodfit.data.api.service.ApertiumApiService
import com.mariana.foodfit.data.api.service.EdamanApiService
import com.mariana.foodfit.data.api.service.MercadonaApiService
import com.mariana.foodfit.data.api.service.TheMealApiService
import com.mariana.foodfit.data.entity.Ingrediente
import com.mariana.foodfit.data.entity.Platillo
import com.mariana.foodfit.data.service.IngredienteService
import com.mariana.foodfit.data.service.PlatilloService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Clase encargada de construir y registrar platillos completos en Firestore,
 * incluyendo sus ingredientes, propiedades nutricionales y fotos.
 */
class PlatillosSeeder(
    private val platilloService: PlatilloService = PlatilloService(),
    private val ingredienteService: IngredienteService = IngredienteService(),
    private val theMealApiService: TheMealApiService = TheMealApiService(),
    private val mercadonaApiService: MercadonaApiService = MercadonaApiService(),
    private val edamanApiService: EdamanApiService = EdamanApiService(),
    private val apertiumApiService: ApertiumApiService = ApertiumApiService()
) {

    /**
     * Método que crea un platillo completo si no existe previamente en la base de datos.
     * Agrega los ingredientes y obtiene la información nutricional y visual correspondiente.
     */
    suspend fun crearPlatilloCompleto(
        nombrePlatillo: String,
        categoria: String,
        ingredientesPlatillo: List<IngredientePlatillo>,
        pasosPreparacion: List<String>,
        isFavorito: Boolean,
        creadoPor: String,
    ) = withContext(Dispatchers.IO) {

        val ingredientesList = mutableListOf<IngredientePlatillo>()

        for (ingredienteInfo in ingredientesPlatillo) {
            val nombreIngrediente = ingredienteInfo.nombre
            val cantidad = ingredienteInfo.cantidad
            val unidad = ingredienteInfo.unidad

            var ingredienteFirestore = ingredienteService.getIngredienteByNombre(nombreIngrediente)

            if (ingredienteFirestore == null) {
                val ingredienteMercadona =
                    mercadonaApiService.buscarIngredientePorNombre(nombreIngrediente)

                if (ingredienteMercadona == null) {
                    Log.w(
                        "PlatilloSeeder",
                        "Ingrediente no encontrado en MercadonaApi: $nombreIngrediente"
                    )
                    return@withContext
                }

                val nombIngredienteTraducido =
                    apertiumApiService.traducirATextoIngles(nombreIngrediente)
                Log.d(
                    "PlatillosSeeder",
                    "Nombre traducido: $nombreIngrediente -> $nombIngredienteTraducido"
                )

                val nutrientesEdaman = edamanApiService.obtenerNutrientes(nombIngredienteTraducido)
                Log.d("PlatillosSeeder", "Nutrientes obtenidos: $nutrientesEdaman")

                ingredienteFirestore = Ingrediente(
                    idIngrediente = ingredienteMercadona["idIngrediente"] as String,
                    nombre = ingredienteMercadona["nombre"] as String,
                    precio = ingredienteMercadona["precio"] as Double,
                    fotoUrl = ingredienteMercadona["fotoUrl"] as String,
                    calorias = nutrientesEdaman["calorias"] as Double,
                    proteinas = nutrientesEdaman["proteinas"] as Double
                )

                val idIngrediente = ingredienteService.addIngrediente(ingredienteFirestore)
                Log.d("PlatillosSeeder", "Ingrediente guardado en Firestore con ID $idIngrediente")
            } else {
                Log.d(
                    "PlatilloSeeder",
                    "Ingrediente ya existe en Firestore: ${ingredienteFirestore.nombre}"
                )
            }

            // Guardamos la relación con cantidad y unidad
            val ingredientePlatilloFinal = IngredientePlatillo(
                nombre = ingredienteFirestore.nombre,
                cantidad = cantidad,
                unidad = unidad
            )
            ingredientesList.add(ingredientePlatilloFinal)

            delay(5000)
        }

        // Obtenemos la foto del platillo al final
        val nombPlatilloTraducido = apertiumApiService.traducirATextoIngles(nombrePlatillo)
        Log.d(
            "PlatillosSeeder",
            "Nombre platillo traducido: $nombrePlatillo -> $nombPlatilloTraducido"
        )

        var fotoPlatillo = theMealApiService.obtenerFotoPorNombre(nombPlatilloTraducido)
        if (fotoPlatillo == null) {
            fotoPlatillo =
                "https://cdni.iconscout.com/illustration/free/thumb/free-cocinar-comida-1654583-1399845.png"
            Log.d("PlatillosSeeder", "Foto platillo no obtenida, usando por defecto: $fotoPlatillo")
        }

        // Finalmente creamos el platillo completo
        val platillo = Platillo(
            nombre = nombrePlatillo,
            categoria = categoria,
            fotoUrl = fotoPlatillo,
            pasosPreparacion = pasosPreparacion,
            isFavorite = isFavorito,
            creadoPor = creadoPor,
            ingredientes = ingredientesList  // Lista completa de ingredientes con cantidad y unidad
        )

        val idPlatillo = platilloService.addPlatillo(platillo)
        Log.d("PlatillosSeeder", "Platillo guardado en Firestore con ID $idPlatillo")
    }

}
