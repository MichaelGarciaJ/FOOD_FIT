package com.mariana.foodfit.ui.adapters.platilloView

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.ui.meals.model.PlatilloVistaItem

/**
 * Callback personalizado para optimizar las actualizaciones del RecyclerView que muestra platillos.
 * Utiliza DiffUtil para calcular diferencias entre listas de PlatilloVistaItem y actualizar solo los elementos necesarios.
 */
class PlatilloDiffCallback : DiffUtil.ItemCallback<PlatilloVistaItem>() {

    /**
     * Método que determina si dos objetos PlatilloVistaItem representan el mismo ítem, comparando sus IDs.
     *
     * @param oldItem Elemento antiguo de la lista.
     * @param newItem Elemento nuevo de la lista.
     * @return true si ambos ítems tienen el mismo ID, false en caso contrario.
     */
    override fun areItemsTheSame(oldItem: PlatilloVistaItem, newItem: PlatilloVistaItem): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Método que determina si el contenido de dos objetos PlatilloVistaItem es igual.
     * Esto ayuda a evitar actualizaciones innecesarias en el RecyclerView.
     *
     * @param oldItem Elemento antiguo.
     * @param newItem Elemento nuevo.
     * @return true si todos los campos del objeto son iguales, false en caso contrario.
     */
    override fun areContentsTheSame(
        oldItem: PlatilloVistaItem,
        newItem: PlatilloVistaItem
    ): Boolean {
        return oldItem == newItem
    }
}