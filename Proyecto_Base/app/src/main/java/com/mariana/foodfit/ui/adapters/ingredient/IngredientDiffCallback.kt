package com.mariana.foodfit.ui.adapters.ingredient

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.model.Ingredient

/**
 * Callback para DiffUtil que compara ingredientes en la lista
 * para optimizar la actualización del RecyclerView.
 */
class IngredientDiffCallback : DiffUtil.ItemCallback<Ingredient>() {

    /**
     * Método que determina si dos ingredientes representan el mismo ítem,
     * comparando sus nombres.
     *
     * @param oldItem Ingrediente antiguo.
     * @param newItem Ingrediente nuevo.
     * @return true si ambos tienen el mismo nombre.
     */
    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem.nombre == newItem.nombre
    }

    /**
     * Método que determina si el contenido completo de dos ingredientes es igual.
     *
     * @param oldItem Ingrediente antiguo.
     * @param newItem Ingrediente nuevo.
     * @return true si ambos ingredientes son iguales en contenido.
     */
    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem == newItem
    }

}