package com.mariana.foodfit.ui.adapters.comment

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.entity.Comentario


/**
 * Callback para DiffUtil que optimiza la actualización del RecyclerView de comentarios.
 */
class CommentDiffCallback : DiffUtil.ItemCallback<Comentario>() {

    /**
     * Método que determina si dos comentarios representan el mismo ítem,
     * comparando su identificador único.
     *
     * @param oldItem Comentario antiguo.
     * @param newItem Comentario nuevo.
     * @return true si ambos comentarios tienen el mismo uid.
     */
    override fun areItemsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
        return oldItem.uid == newItem.uid
    }

    /**
     * Método que comprueba si el contenido completo de dos comentarios es igual.
     *
     * @param oldItem Comentario antiguo.
     * @param newItem Comentario nuevo.
     * @return true si ambos comentarios son iguales en todos sus campos.
     */
    override fun areContentsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
        return oldItem == newItem
    }

}