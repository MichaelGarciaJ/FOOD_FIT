package com.mariana.foodfit.ui.adapters.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.Comentario
import com.mariana.foodfit.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(
    private val currentUserId: String,
    private val onDeleteClick: (Comentario) -> Unit
) : ListAdapter<Comentario, CommentAdapter.ComentarioViewHolder>(CommentDiffCallback()) {

    inner class ComentarioViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: Comentario) {
            binding.itemCommentUsername.text = comentario.nombreUsuario
            binding.itemCommentText.text = comentario.texto

            if (comentario.fotoUsuario.isNotEmpty()) {
                Glide.with(binding.itemCommentUserAvatar.context)
                    .load(comentario.fotoUsuario)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.itemCommentUserAvatar)
            } else {
                binding.itemCommentUserAvatar.setImageResource(R.drawable.ic_person)
            }

            binding.itemCommentTimestamp.text = formatFecha(comentario.fecha)

            // Mostrar botón solo si es su comentario
            if (comentario.uid == currentUserId) {
                binding.itemCommentDeleteButton.visibility = View.VISIBLE
                binding.itemCommentDeleteButton.setOnClickListener {
                    onDeleteClick(comentario)
                }
            } else {
                binding.itemCommentDeleteButton.visibility = View.GONE
            }
        }

        private fun formatFecha(fecha: Timestamp?): String {
            return if (fecha != null) {
                val date = fecha.toDate()
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                sdf.format(date)
            } else {
                "Fecha desconocida"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComentarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


