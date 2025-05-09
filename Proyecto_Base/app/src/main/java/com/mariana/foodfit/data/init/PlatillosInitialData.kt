package com.mariana.foodfit.data.init

data object PlatillosInitialData {

    fun obtenerPlatillosIniciales(): List<PlatilloInicial> {
        return listOf(
            PlatilloInicial(
                nombre = "Tortilla de claras con espinacas y pavo",
                categoria = "Desayuno",
                ingredientes = listOf(
                    IngredientePlatillo("Claras de huevo líquidas pasteurizadas", 1, "200 ml"),
                    IngredientePlatillo("Espinaca baby", 1, "50 g"),
                    IngredientePlatillo(
                        "Pechuga de pavo bajo en sal Hacendado finas lonchas",
                        1,
                        "50 g"
                    )
                ),
                pasos = listOf(
                    "1. Calienta una sartén antiadherente con un chorrito de aceite (5 ml).",
                    "2. Añade las espinacas y saltéalas 2-3 minutos hasta que se reduzcan.",
                    "3. Agrega las claras de huevo, sazona con sal y pimienta, y cocina a fuego medio.",
                    "4. Cuando empiece a cuajar, añade el pavo cortado en trocitos.",
                    "5. Dobla como tortilla, cocina 1 minuto más, ¡y listo!"
                )
            ),
            PlatilloInicial(
                nombre = "Yogur griego con frutos rojos, avena y semillas",
                categoria = "Desayuno",
                ingredientes = listOf(
                    IngredientePlatillo("Yogur griego ligero natural Hacendado", 1, ""),
                    IngredientePlatillo("Copos de avena Brüggen", 1, "30 g"),
                    IngredientePlatillo("Frambuesa", 1, "50 g"),
                    IngredientePlatillo("Semillas de chía Hacendado", 1, "10 g"),
                    IngredientePlatillo("Miel de flores Hacendado", 1, "5 ml")
                ),
                pasos = listOf(
                    "1. Coloca el yogur en un bol.",
                    "2. Añade por encima la avena, las frambuesas y las semillas de chía.",
                    "3. Si quieres, añade un chorrito de miel para endulzar.",
                    "4. Remueve suavemente o deja en capas, ¡y disfruta!"
                )
            ),
        )
    }
}