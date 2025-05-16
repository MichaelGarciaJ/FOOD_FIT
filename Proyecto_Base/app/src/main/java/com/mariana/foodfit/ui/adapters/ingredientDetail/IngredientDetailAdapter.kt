package com.mariana.foodfit.ui.adapters.ingredientDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.IngredientDetail

/**
 * Adaptador para RecyclerView que muestra detalles de ingredientes nutricionales.
 * Utiliza ListAdapter con DiffUtil para optimizar las actualizaciones.
 */
class IngredientDetailAdapter :
    ListAdapter<IngredientDetail, IngredientDetailAdapter.DetailViewHolder>(IngredientDetailDiffCallback()) {

    /**
     * ViewHolder que representa cada ítem de detalle de ingrediente.
     *
     * @param view Vista raíz del ítem.
     */
    inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.itemDetailIngredientName)
        val calories: TextView = view.findViewById(R.id.itemDetailCalories)
        val fat: TextView = view.findViewById(R.id.itemDetailFat)
        val carbs: TextView = view.findViewById(R.id.itemDetailCarbs)
        val fiber: TextView = view.findViewById(R.id.itemDetailFiber)
        val protein: TextView = view.findViewById(R.id.itemDetailProtein)
    }

    /**
     * Método que infla el layout para el ítem de detalle de ingrediente y crea el ViewHolder correspondiente.
     *
     * @param parent Vista padre donde se insertará el ítem.
     * @param viewType Tipo de vista (no usado).
     * @return Nueva instancia de DetailViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_ingredient, parent, false)
        return DetailViewHolder(view)
    }

    /**
     * Método que vincula los datos del ingrediente a las vistas del ViewHolder.
     *
     * @param holder ViewHolder que contiene las vistas a actualizar.
     * @param position Posición del ítem en la lista.
     */
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
