package com.mariana.foodfit.ui.adapters.platilloView

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariana.foodfit.R
import com.mariana.foodfit.ui.meals.model.PlatilloVistaItem
import com.mariana.foodfit.ui.meals.foodPlateInfo.FoodPlateInfoActivity

/**
 * Adaptador que gestiona la visualización de una lista de platillos en un RecyclerView.
 * Utiliza DiffUtil para mejorar el rendimiento al actualizar elementos de la lista.
 *
 * @param onFavoriteClick Función callback que se invoca cuando el usuario presiona el ícono de favorito.
 */
class PlatilloVistaAdapter(
    private val onFavoriteClick: (PlatilloVistaItem) -> Unit
) : ListAdapter<PlatilloVistaItem, PlatilloVistaAdapter.FoodViewHolder>(PlatilloDiffCallback()) {

    // Variable para bloquear clics
    private var isClickLocked = false

    /**
     * ViewHolder personalizado que representa cada ítem (platillo) de la lista.
     *
     * @param itemView Vista raíz que contiene los elementos visuales del platillo.
     */
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemFoodImage)
        val title: TextView = itemView.findViewById(R.id.itemFoodTitle)
        val subtitle: TextView = itemView.findViewById(R.id.itemFoodSubtitle)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.itemFoodFavoriteIcon)

        /**
         * Método que vincula los datos de un PlatilloVistaItem con las vistas del layout.
         *
         * @param item Objeto PlatilloVistaItem que contiene la información del platillo.
         * @param onFavoriteClick Callback que se ejecuta al pulsar el ícono de favoñrito.
         */
        fun bind(item: PlatilloVistaItem, onFavoriteClick: (PlatilloVistaItem) -> Unit, onItemClick: (PlatilloVistaItem) -> Unit) {
            Glide.with(image.context)
                .load(item.fotoUrl)
                .placeholder(R.drawable.ic_food_no)
                .error(R.drawable.ic_cancel)
                .into(image)

            title.text = item.title
            subtitle.text = item.subtitle

            favoriteIcon.setImageResource(
                if (item.isFavorite) R.drawable.ic_favorite_background else R.drawable.ic_favorite
            )

            favoriteIcon.setOnClickListener {
                onFavoriteClick(item)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    /**
     * Método que crea nuevas instancias de ViewHolder para el RecyclerView.
     *
     * @param parent Vista padre en la que se colocará el nuevo ítem.
     * @param viewType Tipo de vista (no usado en este caso).
     * @return Instancia de FoodViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    /**
     * Método que enlaza los datos de un PlatilloVistaItem a su correspondiente ViewHolder.
     *
     * @param holder ViewHolder que representa la vista del ítem.
     * @param position Posición del ítem en la lista.
     */
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, onFavoriteClick) { clickedItem ->
            // Prevenir múltiples clics
            if (isClickLocked) return@bind

            // Bloquear clics
            isClickLocked = true

            // Abrir la actividad con un pequeño retraso para permitir la animación de transición
            val context = holder.itemView.context
            val intent = Intent(context, FoodPlateInfoActivity::class.java)

            // Pasar datos
            Log.d("PlatilloVistaAdapter", "Platillo: ${clickedItem.id}")
            intent.putExtra("PLATILLO", clickedItem.id)
            context.startActivity(intent)

            // Liberar el bloqueo de clic después de un pequeño retraso (500ms)
            holder.itemView.postDelayed({
                isClickLocked = false
            }, 500)
        }

    }
}