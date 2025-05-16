package com.mariana.foodfit.ui.adapters.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mariana.foodfit.R
import com.mariana.foodfit.data.init.IngredientePlatillo

/**
 * Adaptador para RecyclerView que muestra ingredientes de un platillo,
 * permitiendo eliminar ingredientes mediante un botón.
 *
 * @param ingredientes Lista mutable de ingredientes del platillo.
 * @param onDelete Callback que se ejecuta al eliminar un ingrediente.
 */
class IngredientPlatilloAdapter(
    private val ingredientes: MutableList<IngredientePlatillo>,
    private val onDelete: (IngredientePlatillo) -> Unit
) : RecyclerView.Adapter<IngredientPlatilloAdapter.ViewHolder>() {

    /**
     * ViewHolder que representa cada ingrediente con opción a eliminar.
     *
     * @param itemView Vista raíz del ítem.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreText: TextView = itemView.findViewById(R.id.ingredientName)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnRemoveIngredient)
    }

    /**
     * Método que infla el layout para el ítem con botón de eliminar y crea el ViewHolder.
     *
     * @param parent Vista padre donde se insertará el ítem.
     * @param viewType Tipo de vista (no usado).
     * @return Nueva instancia de ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_with_delete, parent, false)
        return ViewHolder(view)
    }

    /**
     * Métodoo que vincula los datos del ingrediente y el listener para eliminarlo.
     *
     * @param holder ViewHolder con las vistas a actualizar.
     * @param position Posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingrediente = ingredientes[position]
        holder.nombreText.text = ingrediente.nombre
        holder.btnEliminar.setOnClickListener { onDelete(ingrediente) }
    }

    /**
     * Método que devuelve el tamaño actual de la lista de ingredientes.
     *
     * @return Número de ingredientes en la lista.
     */
    override fun getItemCount(): Int = ingredientes.size

}

