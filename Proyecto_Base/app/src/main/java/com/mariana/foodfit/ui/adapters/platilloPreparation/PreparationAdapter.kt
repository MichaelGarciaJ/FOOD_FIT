package com.mariana.foodfit.ui.adapters.platilloPreparation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.ui.meals.model.PreparationStep

class PreparationAdapter :
    ListAdapter<PreparationStep, PreparationAdapter.PreparationViewHolder>(PreparationDiffCallback()) {

    inner class PreparationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepText: TextView = view.findViewById(R.id.itemPreparationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preparation, parent, false)
        return PreparationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreparationViewHolder, position: Int) {
        holder.stepText.text = getItem(position).texto
    }
}
