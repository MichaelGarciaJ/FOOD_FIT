package com.mariana.foodfit.ui.adapters.platilloPreparation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mariana.foodfit.R

/**
 * Adaptador para un RecyclerView que muestra una lista de pasos de preparación
 * con opción para eliminar cada paso.
 *
 * @param pasos Lista mutable de strings que representa los pasos de preparación.
 * @param onDelete Callback que se invoca cuando se elimina un paso, pasando el texto del paso eliminado.
 */
class PreparationStepAdapter(
    private val pasos: MutableList<String>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<PreparationStepAdapter.ViewHolder>() {

    /**
     * ViewHolder que representa la vista para un paso de preparación individual,
     * mostrando el texto del paso y un botón para eliminarlo.
     *
     * @param itemView Vista raíz del ítem del paso.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pasoText: TextView = itemView.findViewById(R.id.preparationName)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnRemovePreparation)
    }

    /**
     * Método que crea un nuevo ViewHolder inflando el layout correspondiente.
     *
     * @param parent Vista padre en la que se insertará el ítem.
     * @param viewType Tipo de vista (no usado en este caso).
     * @return Nuevo ViewHolder creado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preparation_with_delete, parent, false)
        return ViewHolder(view)
    }

    /**
     * Método que vincula los datos del paso de preparación con el ViewHolder.
     * Además, configura el botón de eliminar para llamar al callback onDelete.
     *
     * @param holder ViewHolder que debe ser actualizado.
     * @param position Posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paso = pasos[position]
        holder.pasoText.text = "${position + 1}. ${paso}"
        holder.btnEliminar.setOnClickListener {
            onDelete(paso)
        }
    }

    /**
     * Método que retorna la cantidad total de pasos en la lista.
     *
     * @return Número de ítems (pasos) presentes.
     */
    override fun getItemCount(): Int = pasos.size

}
