package com.mariana.foodfit.ui.adapters.platilloPreparation

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.ui.meals.model.PreparationStep

class PreparationDiffCallback : DiffUtil.ItemCallback<PreparationStep>() {
    override fun areItemsTheSame(oldItem: PreparationStep, newItem: PreparationStep): Boolean {
        return oldItem.texto == newItem.texto
    }

    override fun areContentsTheSame(oldItem: PreparationStep, newItem: PreparationStep): Boolean {
        return oldItem == newItem
    }
}