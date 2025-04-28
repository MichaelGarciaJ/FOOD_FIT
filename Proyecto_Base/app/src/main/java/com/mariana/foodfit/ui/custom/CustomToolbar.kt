package com.mariana.foodfit.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuInflater
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
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
        // Inflar el layout personalizado usando ViewBinding
        binding = ToolbarCustomBinding.inflate(LayoutInflater.from(context), this, true)
    }

    /**
     * Establece el título del toolbar.
     *
     * @param titulo Texto que se mostrará como título.
     */
    fun setTitulo(titulo: String) {
        binding.toolbarTvTitulo.text = titulo
    }

    /**
     * Permite asignar un listener al botón de menú.
     *
     * @param listener Función a ejecutar al hacer clic en el botón menú.
     */
    fun setOnMenuClickListener(listener: () -> Unit) {
        binding.toolbarIbMenu.setOnClickListener { listener() }
    }

    /**
     * Permite asignar un listener al botón de búsqueda.
     *
     * @param listener Función a ejecutar al hacer clic en el botón búsqueda.
     */
    fun setOnBusquedaClickListener(listener: () -> Unit) {
        binding.toolbarIbBusqueda.setOnClickListener { listener() }
    }

    /**
     * Muestra u oculta el botón de búsqueda.
     *
     * @param visible True para mostrar, False para ocultar.
     */
    fun mostrarBusqueda(visible: Boolean) {
        binding.toolbarIbBusqueda.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Muestra u oculta el botón de menú.
     *
     * @param visible True para mostrar, False para ocultar.
     */
    fun mostrarMenu(visible: Boolean) {
        binding.toolbarIbMenu.visibility = if (visible) VISIBLE else GONE
    }

    /**
     * Muestra un PopupMenu anclado al botón de menú.
     *
     * @param menuRes El recurso del menú a inflar.
     * @param onMenuItemClick Acción a ejecutar al seleccionar un ítem.
     */
    fun mostrarPopupMenu(menuRes: Int, onMenuItemClick: (itemId: Int) -> Unit) {
        val popup = PopupMenu(context, binding.toolbarIbMenu)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(menuRes, popup.menu)

        // Forzar mostrar íconos
        try {
            val fields = popup.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popup)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)

                    // Forzar que se muestren separadores entre grupos
                    val setGroupDividerEnabled = classPopupHelper.getMethod(
                        "setGroupDividerEnabled",
                        Boolean::class.javaPrimitiveType
                    )
                    setGroupDividerEnabled.invoke(menuPopupHelper, true)

                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.setOnMenuItemClickListener { menuItem ->
            onMenuItemClick(menuItem.itemId)
            true
        }
        popup.show()
    }

}