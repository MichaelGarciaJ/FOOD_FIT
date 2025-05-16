package com.mariana.foodfit.ui.adapters.ingredient

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.Ingredient

/**
 * Adaptador para RecyclerView que muestra una lista de ingredientes.
 * Utiliza ListAdapter con DiffUtil para mejorar el rendimiento en actualizaciones.
 */
class IngredientAdapter :
    ListAdapter<Ingredient, IngredientAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    /**
     * ViewHolder que representa cada ítem de ingrediente en la lista.
     *
     * @param view Vista raíz del ítem.
     */
    inner class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quantity: TextView = view.findViewById(R.id.itemIngredientQuantity)
        val name: TextView = view.findViewById(R.id.itemIngredientName)
        val unit: TextView = view.findViewById(R.id.itemIngredientUnit)
        val price: TextView = view.findViewById(R.id.itemIngredientPrice)
    }

    /**
     * Método que infla el layout del ítem y crea el ViewHolder.
     *
     * @param parent Vista padre donde se agregará el ítem.
     * @param viewType Tipo de vista (no usado).
     * @return Nueva instancia de IngredientViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    /**
     * Método que vincula los datos del ingrediente con las vistas del ViewHolder.
     *
     * @param holder ViewHolder con las vistas a actualizar.
     * @param position Posición del ítem en la lista.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = getItem(position)
        holder.quantity.text = item.cantidad.toString()
        holder.name.text = item.nombre
        holder.unit.text = item.unidad
        holder.price.text = item.precio.toString() + " €"
    }

}
