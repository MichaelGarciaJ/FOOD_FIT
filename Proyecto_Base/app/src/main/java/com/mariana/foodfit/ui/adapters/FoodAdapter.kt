package com.mariana.foodfit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.FoodItem

class FoodAdapter(private val items: List<FoodItem>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemFoodImage)
        val title: TextView = itemView.findViewById(R.id.itemFoodTitle)
        val subtitle: TextView = itemView.findViewById(R.id.itemFoodSubtitle)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.itemFoodFavoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageResId)
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        val favoriteIconRes = if (item.isFavorite) {
            R.drawable.ic_favorite_background
        } else {
            R.drawable.ic_favorite
        }
        holder.favoriteIcon.setImageResource(favoriteIconRes)

        holder.favoriteIcon.setOnClickListener {
            item.isFavorite = !item.isFavorite
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
