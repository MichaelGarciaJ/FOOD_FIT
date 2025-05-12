package com.mariana.foodfit.ui.adapters.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.ui.meals.model.Ingredient

class IngredientAdapter :
    ListAdapter<Ingredient, IngredientAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    inner class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quantity: TextView = view.findViewById(R.id.itemIngredientQuantity)
        val name: TextView = view.findViewById(R.id.itemIngredientName)
        val unit: TextView = view.findViewById(R.id.itemIngredientUnit)
        val price: TextView = view.findViewById(R.id.itemIngredientPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = getItem(position)
        holder.quantity.text = item.cantidad.toString()
        holder.name.text = item.nombre
        holder.unit.text = item.unidad
        holder.price.text = item.precio.toString()
    }
}
