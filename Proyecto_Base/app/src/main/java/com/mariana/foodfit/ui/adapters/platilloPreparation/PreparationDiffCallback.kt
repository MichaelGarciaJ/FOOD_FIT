package com.mariana.foodfit.ui.adapters.platilloPreparation

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.model.PreparationStep

/**
 * Callback personalizado para DiffUtil que ayuda a optimizar
 * las actualizaciones de listas en RecyclerView para objetos
 * de tipo "PreparationStep".
 */
class PreparationDiffCallback : DiffUtil.ItemCallback<PreparationStep>() {

    /**
     * Método que verifica si dos ítems representan el mismo paso de preparación,
     * comparando su texto único.
     *
     * @param oldItem Paso de preparación anterior.
     * @param newItem Paso de preparación nuevo.
     * @return true si ambos ítems representan el mismo paso, false en caso contrario.
     */
    override fun areItemsTheSame(oldItem: PreparationStep, newItem: PreparationStep): Boolean {
        return oldItem.texto == newItem.texto
    }

    /**
     * Método que compara el contenido completo de dos pasos de preparación para detectar
     * cambios en sus propiedades.
     *
     * @param oldItem Paso de preparación anterior.
     * @param newItem Paso de preparación nuevo.
     * @return true si el contenido de ambos pasos es idéntico, false si hay diferencias.
     */
    override fun areContentsTheSame(oldItem: PreparationStep, newItem: PreparationStep): Boolean {
        return oldItem == newItem
    }

}