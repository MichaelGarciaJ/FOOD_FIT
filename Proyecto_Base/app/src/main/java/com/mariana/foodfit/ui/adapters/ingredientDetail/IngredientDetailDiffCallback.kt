package com.mariana.foodfit.ui.adapters.ingredientDetail

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.model.IngredientDetail

/**
 * Callback para DiffUtil que optimiza las actualizaciones de la lista
 * comparando los detalles de ingredientes.
 */
class IngredientDetailDiffCallback : DiffUtil.ItemCallback<IngredientDetail>() {

    /**
     * Método que compara si dos ítems representan el mismo ingrediente por su nombre.
     *
     * @param oldItem Ítem antiguo.
     * @param newItem Ítem nuevo.
     * @return true si ambos ítems representan el mismo ingrediente.
     */
    override fun areItemsTheSame(oldItem: IngredientDetail, newItem: IngredientDetail): Boolean {
        return oldItem.nombre == newItem.nombre
    }

    /**
     * Método que compara el contenido completo de dos ítems para detectar cambios.
     *
     * @param oldItem Ítem antiguo.
     * @param newItem Ítem nuevo.
     * @return true si ambos ítems tienen el mismo contenido.
     */
    override fun areContentsTheSame(oldItem: IngredientDetail, newItem: IngredientDetail): Boolean {
        return oldItem == newItem
    }

}