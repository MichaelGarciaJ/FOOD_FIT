package com.mariana.foodfit.ui.adapters.preparation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mariana.foodfit.R

class PreparationStepAdapter(
    private val pasos: MutableList<String>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<PreparationStepAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pasoText: TextView = itemView.findViewById(R.id.preparationName)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnRemovePreparation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preparation_with_delete, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paso = pasos[position]
        holder.pasoText.text = "${position + 1}. ${paso}"
        holder.btnEliminar.setOnClickListener {
            onDelete(paso)
        }
    }

    override fun getItemCount(): Int = pasos.size
}
