package com.mariana.foodfit.ui.adapters.ingredientDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.IngredientDetail

class IngredientDetailAdapter :
    ListAdapter<IngredientDetail, IngredientDetailAdapter.DetailViewHolder>(IngredientDetailDiffCallback()) {

    inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemDetailIngredientName)
        val calories: TextView = view.findViewById(R.id.itemDetailCalories)
        val fat: TextView = view.findViewById(R.id.itemDetailFat)
        val carbs: TextView = view.findViewById(R.id.itemDetailCarbs)
        val fiber: TextView = view.findViewById(R.id.itemDetailFiber)
        val protein: TextView = view.findViewById(R.id.itemDetailProtein)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_ingredient, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.nombre
        holder.calories.text = item.calorias.toString()
        holder.fat.text = item.grasas.toString()
        holder.carbs.text = item.carbohidratos.toString()
        holder.fiber.text = item.fibra.toString()
        holder.protein.text = item.proteina.toString()
    }
}
