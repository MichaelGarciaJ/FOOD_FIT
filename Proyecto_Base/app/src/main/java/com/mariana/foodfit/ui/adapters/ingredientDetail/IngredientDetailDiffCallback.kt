package com.mariana.foodfit.ui.adapters.ingredientDetail

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.model.IngredientDetail

class IngredientDetailDiffCallback : DiffUtil.ItemCallback<IngredientDetail>() {
    override fun areItemsTheSame(oldItem: IngredientDetail, newItem: IngredientDetail): Boolean {
        return oldItem.nombre == newItem.nombre
    }

    override fun areContentsTheSame(oldItem: IngredientDetail, newItem: IngredientDetail): Boolean {
        return oldItem == newItem
    }
}