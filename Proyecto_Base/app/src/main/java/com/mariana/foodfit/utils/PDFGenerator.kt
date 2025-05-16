package com.mariana.foodfit.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.mariana.foodfit.data.model.Ingredient
import com.mariana.foodfit.data.model.IngredientDetail
import com.mariana.foodfit.data.model.PlatilloVistaItem
import com.mariana.foodfit.data.model.PreparationStep
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

/**
 * Clase de utilidad encargada de generar un documento PDF con los detalles de una receta,
 * incluyendo información del platillo, ingredientes, información nutricional y pasos de preparación.
 */
object PDFGenerator {

    /**
     * Método que genera un archivo PDF con la información proporcionada de una receta.
     *
     * @param context Contexto de la aplicación, necesario para acceder al almacenamiento.
     * @param platilloVista Objeto que contiene la información principal del platillo (nombre, categoría, etc.).
     * @param ingredients Lista de ingredientes utilizados en la receta.
     * @param ingredientDetails Lista con los detalles nutricionales de cada ingrediente.
     * @param preparationSteps Lista de pasos necesarios para preparar la receta.
     * @return Archivo PDF generado o `null` en caso de error.
     */
    fun generateRecipePDF(
        context: Context,
        platilloVista: PlatilloVistaItem?,
        ingredients: List<Ingredient>,
        ingredientDetails: List<IngredientDetail>,
        preparationSteps: List<PreparationStep>
    ): File? {
        val pageWidth = 595
        val pageHeight = 842
        val pdfDocument = PdfDocument()

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Configuración de colores
        val primaryColor = Color.parseColor("#3B6939")
        val secondaryColor = Color.parseColor("#4B662C")
        val surfaceColor = Color.WHITE
        val onSurfaceColor = Color.parseColor("#191D17")
        val outlineColor = Color.parseColor("#72796F")

        // Pintar fondo blanco
        val backgroundPaint = Paint().apply {
            color = surfaceColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), backgroundPaint)

        // Función para crear nueva página
        fun createNewPage(): Canvas {
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            val newCanvas = page.canvas
            newCanvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), backgroundPaint)
            return newCanvas
        }

        // Configuración de estilos de texto
        val textPaint = Paint().apply {
            color = onSurfaceColor
            textSize = 11f
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
        }

        val tableHeaderPaint = Paint(textPaint).apply {
            isFakeBoldText = true
            color = Color.WHITE
        }

        val sectionPaint = Paint(textPaint).apply {
            textSize = 14f
            isFakeBoldText = true
            color = secondaryColor
        }

        // Dibujar título
        val titlePaint = Paint(textPaint).apply {
            textAlign = Paint.Align.CENTER
            textSize = 22f
            isFakeBoldText = true
            color = primaryColor
        }

        // Configuración de márgenes y espaciado
        val leftMargin = 50f
        val rightMargin = 50f
        val usableWidth = pageWidth - leftMargin - rightMargin
        val lineSpacing = 20f
        val paragraphSpacing = 30f
        val sectionSpacing = 40f
        var yPosition = 50f
        val bottomMargin = 50f

        // Función mejorada para verificar espacio y crear nueva página si es necesario
        fun checkSpace(requiredHeight: Float): Boolean {
            val earlyBreakThreshold = requiredHeight * 0.1f
            if (yPosition + requiredHeight > pageHeight - bottomMargin - earlyBreakThreshold) {
                canvas = createNewPage()
                yPosition = 50f
                return true
            }
            return false
        }

        // Función para dibujar texto con manejo de múltiples líneas y saltos de página
        fun drawTextLine(
            text: String,
            paint: Paint,
            x: Float = leftMargin,
            maxWidth: Float = usableWidth
        ): Float {
            val lines = breakTextIntoLines(text, paint, maxWidth)
            for (line in lines) {
                if (checkSpace(lineSpacing)) {
                    if (text == "FOOD FIT") {
                        canvas.drawText(text, pageWidth / 2f, yPosition, titlePaint)
                    }
                }
                canvas.drawText(line, x, yPosition, paint)
                yPosition += lineSpacing
            }
            return yPosition
        }

        // Función para dibujar texto centrado con manejo de saltos de página
        fun drawCenteredText(text: String, paint: Paint) {
            val lines = breakTextIntoLines(text, paint, usableWidth)
            for (line in lines) {
                val x = (pageWidth - paint.measureText(line)) / 2
                canvas.drawText(line, x, yPosition, paint)
                yPosition += lineSpacing
            }
        }

        // Función mejorada para dibujar tablas con saltos de línea y manejo de páginas
        fun drawStyledTable(
            headers: List<String>,
            rows: List<List<String>>,
            columnWidths: List<Float>,
            headerColor: Int,
            rowColor: Int,
            borderColor: Int
        ) {
            val tableWidth = columnWidths.sum()
            val startX = (pageWidth - tableWidth) / 2f

            // Calcular alturas de filas considerando múltiples líneas
            val rowHeights = rows.map { row ->
                row.mapIndexed { index, cellText ->
                    val lines = breakTextIntoLines(cellText, textPaint, columnWidths[index] - 8f)
                    lines.size * lineSpacing * 1.2f
                }.maxOrNull() ?: lineSpacing
            }

            val headerHeight = lineSpacing * 1.5f

            // Dibujar encabezado de tabla
            val headerPaint = Paint().apply {
                color = headerColor
                style = Paint.Style.FILL
            }
            canvas.drawRect(
                startX,
                yPosition,
                startX + tableWidth,
                yPosition + headerHeight,
                headerPaint
            )

            var x = startX
            for (i in headers.indices) {
                canvas.drawText(
                    headers[i],
                    x + 4f,
                    yPosition + headerHeight * 0.7f,
                    tableHeaderPaint
                )
                x += columnWidths[i]
            }
            yPosition += headerHeight

            // Dibujar filas de la tabla
            val rowPaint = Paint().apply {
                color = rowColor
                style = Paint.Style.FILL
            }
            val borderPaint = Paint().apply {
                color = borderColor
                strokeWidth = 0.5f
                style = Paint.Style.STROKE
            }

            for ((rowIndex, row) in rows.withIndex()) {
                val rowHeight = rowHeights[rowIndex]

                // Verificar espacio para la fila completa
                if (checkSpace(rowHeight)) {
                    // Si cambiamos de página, debemos volver a dibujar los encabezados de tabla
                    x = startX
                    for (i in headers.indices) {
                        canvas.drawText(
                            headers[i],
                            x + 4f,
                            yPosition + headerHeight * 0.7f,
                            tableHeaderPaint
                        )
                        x += columnWidths[i]
                    }
                    yPosition += headerHeight
                }

                // Dibujar fila
                canvas.drawRect(
                    startX,
                    yPosition,
                    startX + tableWidth,
                    yPosition + rowHeight,
                    rowPaint
                )

                x = startX
                for ((colIndex, cellText) in row.withIndex()) {
                    val lines = breakTextIntoLines(cellText, textPaint, columnWidths[colIndex] - 8f)
                    var lineY = yPosition + lineSpacing * 0.8f

                    for (line in lines) {
                        canvas.drawText(line, x + 4f, lineY, textPaint)
                        lineY += lineSpacing * 1.2f
                    }
                    x += columnWidths[colIndex]
                }

                // Dibujar bordes
                canvas.drawRect(
                    startX,
                    yPosition,
                    startX + tableWidth,
                    yPosition + rowHeight,
                    borderPaint
                )

                // Dibujar líneas verticales
                x = startX
                for (width in columnWidths) {
                    canvas.drawLine(x, yPosition, x, yPosition + rowHeight, borderPaint)
                    x += width
                }
                canvas.drawLine(x, yPosition, x, yPosition + rowHeight, borderPaint)

                yPosition += rowHeight
            }
            yPosition += paragraphSpacing / 2
        }

        canvas.drawText("FOOD FIT", pageWidth / 2f, yPosition, titlePaint)
        yPosition += lineSpacing * 0.8f

        // Nombre del platillo (centrado)
        platilloVista?.let { platillo ->
            val dishNamePaint = Paint(textPaint).apply {
                textAlign = Paint.Align.LEFT
                textSize = 16f
                isFakeBoldText = true
                color = secondaryColor
            }

            yPosition += lineSpacing
            drawCenteredText(platillo.title, dishNamePaint)
            yPosition += lineSpacing
        }

        // Categoría
        platilloVista?.let { platillo ->
            canvas.drawText("Categoría: ${platillo.subtitle}", leftMargin, yPosition, sectionPaint)
            yPosition += paragraphSpacing
        }

        // Tabla Ingredientes
        yPosition += sectionSpacing / 2
        canvas.drawText("Ingredientes", leftMargin, yPosition, sectionPaint)
        yPosition += lineSpacing
        val ingColWidths = listOf(50f, 250f, 60f, 60f)
        drawStyledTable(
            headers = listOf("Cant.", "Ingrediente", "Unidad", "Precio"),
            rows = ingredients.map {
                listOf(
                    it.cantidad.toString(),
                    it.nombre,
                    it.unidad,
                    "${it.precio}€"
                )
            },
            columnWidths = ingColWidths,
            headerColor = primaryColor,
            rowColor = Color.WHITE,
            borderColor = outlineColor
        )

        // Tabla Nutricional
        yPosition += sectionSpacing
        canvas.drawText("Detalles Nutricionales", leftMargin, yPosition, sectionPaint)
        yPosition += lineSpacing
        val nutriColWidths = listOf(150f, 60f, 60f, 60f, 60f, 60f)
        drawStyledTable(
            headers = listOf("Ingrediente", "Kcal", "Grasas", "Carbs", "Fibra", "Prot."),
            rows = ingredientDetails.map {
                listOf(
                    it.nombre,
                    it.calorias.toString(),
                    "${it.grasas}g",
                    "${it.carbohidratos}g",
                    "${it.fibra}g",
                    "${it.proteina}g"
                )
            },
            columnWidths = nutriColWidths,
            headerColor = primaryColor,
            rowColor = Color.WHITE,
            borderColor = outlineColor
        )

        // Preparación
        yPosition += sectionSpacing
        canvas.drawText("Preparación", leftMargin, yPosition, sectionPaint)
        yPosition += lineSpacing
        preparationSteps.forEachIndexed { index, step ->
            val stepText = "${step.texto}"
            yPosition = drawTextLine(stepText, textPaint)
            yPosition += lineSpacing / 2
        }

        // Pie de página centrado
        yPosition = pageHeight - bottomMargin
        val footerPaint = Paint(textPaint).apply {
            color = outlineColor
            textSize = 9f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "© ${SimpleDateFormat("yyyy").format(Date())} FoodFit",
            pageWidth / 2f,
            yPosition,
            footerPaint
        )

        // Finalizar documento
        pdfDocument.finishPage(page)

        // Guardar archivo
        val fileName = "${platilloVista?.title?.replace(" ", "_") ?: ""}.pdf"
        val pdfFile = File(context.getExternalFilesDir(null), fileName)

        return try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            pdfDocument.close()
            pdfFile
        } catch (e: IOException) {
            Log.e("PDF", "Error al guardar el PDF: ${e.message}")
            Utils.mostrarMensaje(context, "No se ha podido generar el PDF")
            null
        }
    }

    /**
     * Método auxiliar que divide un texto largo en múltiples líneas para ajustarlo al ancho máximo permitido,
     * asegurando que no se corte y se mantenga legible en el PDF.
     *
     * @param text Texto original a dividir.
     * @param paint Estilo de pintura utilizado para medir el ancho del texto.
     * @param maxWidth Ancho máximo permitido por línea.
     * @return Lista de líneas de texto divididas correctamente según el ancho.
     */
    private fun breakTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (text.isEmpty()) return listOf("")

        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in text.split(" ")) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }
}