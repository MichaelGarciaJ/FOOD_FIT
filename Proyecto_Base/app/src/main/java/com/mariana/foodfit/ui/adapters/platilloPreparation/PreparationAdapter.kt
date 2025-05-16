package com.mariana.foodfit.ui.adapters.platilloPreparation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.model.PreparationStep

/**
 * Adaptador para un RecyclerView que muestra una lista de pasos de preparación de un platillo.
 * Utiliza "PreparationDiffCallback" para optimizar las actualizaciones.
 */
class PreparationAdapter :
    ListAdapter<PreparationStep, PreparationAdapter.PreparationViewHolder>(PreparationDiffCallback()) {

    /**
     * ViewHolder que contiene la vista para mostrar un paso de preparación.
     *
     * @param view Vista raíz del ítem de preparación.
     */
    inner class PreparationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepText: TextView = view.findViewById(R.id.itemPreparationText)
    }

    /**
     * Método que crea una nueva instancia de "PreparationViewHolder".
     *
     * @param parent Vista padre que contendrá el ítem.
     * @param viewType Tipo de vista (no usado en este caso).
     * @return Un nuevo objeto "PreparationViewHolder".
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreparationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preparation, parent, false)
        return PreparationViewHolder(view)
    }

    /**
     * Método que vincula los datos del paso de preparación a la vista del ViewHolder.
     *
     * @param holder ViewHolder que mostrará el dato.
     * @param position Posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: PreparationViewHolder, position: Int) {
        holder.stepText.text = getItem(position).texto
    }

}
