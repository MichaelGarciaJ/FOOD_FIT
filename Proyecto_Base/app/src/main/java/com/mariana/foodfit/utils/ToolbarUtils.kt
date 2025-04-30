package com.mariana.foodfit.utils

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.mariana.foodfit.ui.custom.CustomToolbar

/**
 * Clase de utilidad para configurar toolbars personalizados con drawer navigation.
 */
object ToolbarUtils {

    /**
     * Método que configura el comportamiento del botón de menú en una CustomToolbar para abrir o cerrar el DrawerLayout.
     *
     * @param toolbar Instancia de la toolbar personalizada que tiene el botón de menú.
     * @param drawerLayout DrawerLayout asociado que se desea controlar.
     */
    fun configurarDrawerToggle(toolbar: CustomToolbar, drawerLayout: DrawerLayout) {
        toolbar.setOnMenuClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}
