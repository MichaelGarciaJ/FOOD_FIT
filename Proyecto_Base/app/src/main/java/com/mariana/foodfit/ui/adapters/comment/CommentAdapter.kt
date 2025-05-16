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

/**
 * Adaptador para mostrar una lista de comentarios en un RecyclerView.
 *
 * @param currentUserId ID del usuario actual, usado para mostrar el botón de eliminar solo en sus comentarios.
 * @param onDeleteClick Callback que se ejecuta cuando se pulsa el botón para eliminar un comentario.
 */
class CommentAdapter(
    private val currentUserId: String,
    private val onDeleteClick: (Comentario) -> Unit
) : ListAdapter<Comentario, CommentAdapter.ComentarioViewHolder>(CommentDiffCallback()) {

    /**
     * ViewHolder que representa cada comentario, usando View Binding.
     *
     * @property binding Instancia de ItemCommentBinding para acceder a las vistas.
     */
    inner class ComentarioViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Método que vincula los datos del comentario con las vistas.
         *
         * @param comentario Objeto Comentario con los datos a mostrar.
         */
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

        /**
         * Método que formatea la fecha del comentario a un string legible.
         *
         * @param fecha Fecha en formato Timestamp (Firebase).
         * @return Fecha formateada en "dd/MM/yyyy HH:mm" o texto alternativo si es null.
         */
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

    /**
     * Método que infla la vista para cada comentario y crea el ViewHolder correspondiente.
     *
     * @param parent Vista padre donde se insertará el ítem.
     * @param viewType Tipo de vista (no usado).
     * @return Nuevo ComentarioViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComentarioViewHolder(binding)
    }

    /**
     * Método que vincula el comentario en la posición dada al ViewHolder.
     *
     * @param holder ViewHolder a actualizar.
     * @param position Posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}


