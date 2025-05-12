package com.mariana.foodfit.ui.adapters.ingredient

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.ui.meals.model.Ingredient

class IngredientDiffCallback : DiffUtil.ItemCallback<Ingredient>() {
    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem.nombre == newItem.nombre
    }

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem == newItem
    }
}