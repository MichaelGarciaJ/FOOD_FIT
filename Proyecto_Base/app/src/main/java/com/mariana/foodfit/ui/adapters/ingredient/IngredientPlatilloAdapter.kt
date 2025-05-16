package com.mariana.foodfit.ui.adapters.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mariana.foodfit.R
import com.mariana.foodfit.data.init.IngredientePlatillo

class IngredientPlatilloAdapter(
    private val ingredientes: MutableList<IngredientePlatillo>,
    private val onDelete: (IngredientePlatillo) -> Unit
) : RecyclerView.Adapter<IngredientPlatilloAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreText: TextView = itemView.findViewById(R.id.ingredientName)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnRemoveIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_with_delete, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingrediente = ingredientes[position]
        holder.nombreText.text = ingrediente.nombre
        holder.btnEliminar.setOnClickListener { onDelete(ingrediente) }
    }

    override fun getItemCount(): Int = ingredientes.size
}

