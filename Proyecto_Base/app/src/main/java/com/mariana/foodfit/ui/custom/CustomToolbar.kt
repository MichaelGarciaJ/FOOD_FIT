package com.mariana.foodfit.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.ToolbarCustomBinding

/**
 * CustomToolbar es una versión personalizada de Toolbar que encapsula
 * los elementos comunes como título, iconos de menú y búsqueda.
 */
class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    // ViewBinding para acceder a los elementos de toolbar_custom.xml
    private val binding: ToolbarCustomBinding

    init {
        // Cuando se crea el toolbar, infla su diseño y lo asocia a "binding"
        binding = ToolbarCustomBinding.inflate(LayoutInflater.from(context), this, true)
    }

    /**
     * Método que permite asignar un listener al botón de menú.
     *
     * @param listener Función a ejecutar al hacer clic en el botón menú.
     */
    fun setOnMenuClickListener(listener: () -> Unit) {
        val searchIcon = findViewById<ImageView>(R.id.toolbarIbMenu)
        searchIcon?.setOnClickListener {
            listener()
        }
    }

    /**
     * Método que permite asignar un listener al botón de búsqueda.
     *
     * @param listener Función a ejecutar al hacer clic en el botón búsqueda.
     */
    fun setOnBusquedaClickListener(listener: () -> Unit) {
        val searchIcon = findViewById<ImageView>(R.id.toolbarIbBusqueda)
        searchIcon?.setOnClickListener {
            listener()
        }
    }

    /**
     * Método que muestra u oculta el botón de búsqueda.
     *
     * @param visible True para mostrar, False para ocultar.
     */
    fun mostrarBusqueda(visible: Boolean) {
        binding.toolbarIbBusqueda.visibility = if (visible) VISIBLE else GONE
    }

}