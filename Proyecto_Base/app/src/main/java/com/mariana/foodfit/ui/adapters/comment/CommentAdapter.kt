package com.mariana.foodfit.ui.adapters.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.Comentario
import com.mariana.foodfit.databinding.ItemCommentBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter :
    ListAdapter<Comentario, CommentAdapter.ComentarioViewHolder>(CommentDiffCallback()) {

    inner class ComentarioViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: Comentario) {
            binding.itemCommentUsername.text = comentario.nombreUsuario
            binding.itemCommentText.text = comentario.texto

            // Glide para cargar imagen
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
