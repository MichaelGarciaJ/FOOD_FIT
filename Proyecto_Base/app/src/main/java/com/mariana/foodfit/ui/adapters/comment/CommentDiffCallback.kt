package com.mariana.foodfit.ui.adapters.comment

import androidx.recyclerview.widget.DiffUtil
import com.mariana.foodfit.data.entity.Comentario

class CommentDiffCallback : DiffUtil.ItemCallback<Comentario>() {
    override fun areItemsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
        // Puedes usar el uid del comentario o algún identificador único
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
        // Compara todos los campos si el contenido es igual
        return oldItem == newItem
    }
}