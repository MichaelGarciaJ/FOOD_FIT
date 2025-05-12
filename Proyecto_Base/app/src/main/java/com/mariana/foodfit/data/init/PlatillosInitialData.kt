package com.mariana.foodfit.data.init

/**
 * Objeto singleton que contiene los datos iniciales de platillos.
 * Se utiliza para poblar la base de datos o mostrar ejemplos al usuario por primera vez.
 */
data object PlatillosInitialData {

    /**
     * Método que devuelve una lista de platillos predefinidos categorizados.
     */
    fun obtenerPlatillosIniciales(): List<PlatilloInicial> {
        return listOf(
            // ------------------ DESAYUNO ------------------
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
                    ),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g")
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
                    IngredientePlatillo("Yogur griego ligero natural Hacendado", 1, "125 g"),
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
            PlatilloInicial(
                nombre = "Tostadas integrales con aguacate y huevo duro",
                categoria = "Desayuno",
                ingredientes = listOf(
                    IngredientePlatillo("Pan de molde 100% integral Hacendado", 1, "60 g"),
                    IngredientePlatillo("Aguacate", 1, "70 g"),
                    IngredientePlatillo("Huevos medianos M", 1, "2 und"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g")
                ),
                pasos = listOf(
                    "1. Pon a hervir el huevo durante 10 minutos hasta que esté duro.",
                    "2. Tuesta las rebanadas de pan integral.",
                    "3. Pela y aplasta medio aguacate, añade sal y pimienta.",
                    "4. Unta el aguacate sobre las tostadas.",
                    "5. Pela el huevo, córtalo en rodajas y colócalo encima.",
                    "6. Añade un poco más de pimienta por encima si quieres. ¡Listo!"
                )
            ),
            // ------------------ COMIDA ------------------
            PlatilloInicial(
                nombre = "Pollo a la plancha con quinoa y brócoli",
                categoria = "Comida",
                ingredientes = listOf(
                    IngredientePlatillo("Filetes pechuga de pollo corte fino", 1, "150 g"),
                    IngredientePlatillo("Quinoa Hacendado", 1, "125 g"),
                    IngredientePlatillo("Brócoli Hacendado ultracongelado", 1, "150 g"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g")
                ),
                pasos = listOf(
                    "1. Cocina el brócoli al vapor o en agua hirviendo 5-7 minutos.",
                    "2. Calienta una sartén con el aceite y cocina el pollo 3-4 minutos por lado, sazónalo con sal, pimienta y especias.",
                    "3. Calienta la quinoa en microondas (si es la cocida lista) o cocínala según el paquete si es en grano.",
                    "4. Sirve el pollo junto a la quinoa y el brócoli. ¡Platillo balanceado y rápido!"
                )
            ),
            PlatilloInicial(
                nombre = "Ensalada de atún, garbanzos y huevo duro",
                categoria = "Comida",
                ingredientes = listOf(
                    IngredientePlatillo("Garbanzo cocido Hacendado", 1, "100 g"),
                    IngredientePlatillo("Atún claro al natural Hacendado", 1, "80 g"),
                    IngredientePlatillo("Huevos medianos M", 1, "2 und"),
                    IngredientePlatillo("Tomates cherry", 1, "50 g"),
                    IngredientePlatillo("Ensalada mezcla brotes tiernos", 1, "30 g"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                    IngredientePlatillo("Vinagre de manzana Hacendado", 1, "10 ml")
                ),
                pasos = listOf(
                    "1. Cuece el huevo 10 minutos y déjalo enfriar.",
                    "2. Enjuaga los garbanzos y escúrrelos bien.",
                    "3. Mezcla en un bol los garbanzos, el atún escurrido, los tomates partidos y los brotes.",
                    "4. Pela y corta el huevo duro en rodajas, colócalo encima.",
                    "5. Aliña con aceite, vinagre, sal y pimienta. ¡Fácil, fresco y lleno de proteínas!"
                )
            ),
            PlatilloInicial(
                nombre = "Lomo de salmón al horno con calabaza y espárragos",
                categoria = "Comida",
                ingredientes = listOf(
                    IngredientePlatillo(
                        "Lomos de salmón sin piel y sin espinas Hacendado congelado",
                        1,
                        "150 g "
                    ),
                    IngredientePlatillo("Calabaza cacahuete cortada a trozos", 1, "200 g"),
                    IngredientePlatillo("Espárrago verde fino", 1, "100 g"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal gruesa para hornear Hacendado", 1, "2 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                ),
                pasos = listOf(
                    "1. Precalienta el horno a 200°C.",
                    "2. Coloca el salmón en una bandeja y salpimenta",
                    "3. Coloca a un lado la calabaza en dados y al otro los espárragos (puedes rociarles un poco de aceite).",
                    "4. Hornea todo junto unos 15-20 minutos, hasta que el salmón esté hecho y las verduras tiernas.",
                    "5. Sirve caliente. ¡Un plato ligero, nutritivo y lleno de omega-3!"
                )
            ),
            // ------------------ CENA ------------------
            PlatilloInicial(
                nombre = "Revuelto de champiñones, espinacas y gambas",
                categoria = "Cena",
                ingredientes = listOf(
                    IngredientePlatillo("Champiñones laminados Hacendado", 1, "200 g"),
                    IngredientePlatillo("Espinaca baby", 1, "100 g"),
                    IngredientePlatillo(
                        "Gamba pelada cruda tamaño mediano Hacendado ultracongelada",
                        1,
                        "100g"
                    ),
                    IngredientePlatillo("Huevos medianos M", 1, "2 und"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                ),
                pasos = listOf(
                    "1. Saltea las gambas en una sartén con el aceite durante 2-3 minutos.",
                    "2. Añade los champiñones y cocina 3-4 minutos más.",
                    "3. Agrega las espinacas y deja que se reduzcan.",
                    "4. Bate los huevos, viértelos encima, mezcla bien y cocina a fuego medio hasta que cuajen.",
                    "5. Sazona con sal y pimienta al gusto. ¡Cena ligera y proteica!"
                )
            ),
            PlatilloInicial(
                nombre = "Filete de merluza con calabacín y zanahoria al vapor",
                categoria = "Cena",
                ingredientes = listOf(
                    IngredientePlatillo(
                        "Filetes de merluza del Cabo sin piel Hacendado ultracongelados",
                        1,
                        "150 g"
                    ),
                    IngredientePlatillo("Calabacín verde", 1, "150 g"),
                    IngredientePlatillo("Zanahorias", 1, "100 g"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                ),
                pasos = listOf(
                    "1. Cocina el filete de merluza al vapor (o microondas) durante 6-8 minutos.",
                    "2. Corta el calabacín y la zanahoria en rodajas finas y cocínalos al vapor 5-7 minutos.",
                    "3. Sirve todo junto, añade un chorrito de aceite, sal y pimienta al gusto.",
                    "4. ¡Una cena suave, baja en grasas y muy nutritiva!"
                )
            ),
            PlatilloInicial(
                nombre = "Ensalada templada de pollo, espárragos y aguacate",
                categoria = "Cena",
                ingredientes = listOf(
                    IngredientePlatillo("Filetes pechuga de pollo corte fino", 1, "100 g"),
                    IngredientePlatillo("Espárrago verde fino", 1, "100 g"),
                    IngredientePlatillo("Aguacate", 1, "70 g"),
                    IngredientePlatillo("Ensalada mezcla brotes tiernos", 1, "30 g"),
                    IngredientePlatillo("Aceite de oliva virgen extra Hacendado", 1, "5 ml"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                ),
                pasos = listOf(
                    "1. Cocina el pollo a la plancha con un poco de sal y pimienta, corta en tiras.",
                    "2. Saltea ligeramente los espárragos (o cocínalos al vapor).",
                    "3. En un bol, coloca los brotes verdes, añade el aguacate en cubos, los espárragos y el pollo.",
                    "4. Aliña con aceite, sal y pimienta. ¡Una ensalada nutritiva, saciante y fresca!"
                )
            ),
            // ------------------ SNACK ------------------
            PlatilloInicial(
                nombre = "Palitos de zanahoria con hummus",
                categoria = "Snack",
                ingredientes = listOf(
                    IngredientePlatillo("Zanahorias", 1, "100 g"),
                    IngredientePlatillo("Hummus de garbanzos Hacendado receta clásica", 1, "50 g"),
                ),
                pasos = listOf(
                    "1. Pela la zanahoria y córtala en bastones.",
                    "2. Coloca el hummus en un pequeño bol o recipiente.",
                    "3. Usa los bastones de zanahoria para mojar en el hummus.",
                    "4. ¡Snack crujiente, saludable y lleno de fibra y proteína vegetal!"
                )
            ),
            PlatilloInicial(
                nombre = "Rollitos de pavo con queso fresco y pepino",
                categoria = "Snack",
                ingredientes = listOf(
                    IngredientePlatillo(
                        "Pechuga de pavo bajo en sal Hacendado finas lonchas",
                        1,
                        "50 g"
                    ),
                    IngredientePlatillo(
                        "Queso fresco batido desnatado 0% m.g Hacendado",
                        1,
                        "50 g"
                    ),
                    IngredientePlatillo("Pepino", 1, "75 g"),
                    IngredientePlatillo("Sal fina de mesa Hacendado", 1, "1 g"),
                    IngredientePlatillo("Pimienta negra molida Hacendado", 1, "0.5 g"),
                ),
                pasos = listOf(
                    "1. Pela el pepino y córtalo en bastoncitos finos.",
                    "2. Extiende cada loncha de pavo, unta con una cucharada de queso fresco, coloca unos bastoncitos de pepino.",
                    "3. Enróllalos y añade una pizca de sal, pimienta y unas gotas de limón si quieres.",
                    "4. ¡Snack proteico, bajo en grasa y súper refrescante!"
                )
            ),
            PlatilloInicial(
                nombre = "Yogur griego con nueces y miel",
                categoria = "Snack",
                ingredientes = listOf(
                    IngredientePlatillo("Yogur griego ligero natural Hacendado", 1, "125 g"),
                    IngredientePlatillo("Nuez natural Hacendado pelada", 1, "15 g"),
                    IngredientePlatillo("Miel de flores Hacendado", 1, "5 ml")
                ),
                pasos = listOf(
                    "1. Coloca el yogur en un bol.",
                    "2. Añade las nueces troceadas por encima.",
                    "3. Rocía con la miel.",
                    "4. Mezcla suavemente o deja en capas. ¡Listo para un snack rápido y saciante!"
                )
            )
        )
    }

}