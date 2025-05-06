import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.PlatilloVistaItem

class PlatilloVistaAdapter(
    private val items: List<PlatilloVistaItem>,
    private val onFavoriteClick: (PlatilloVistaItem) -> Unit
) : RecyclerView.Adapter<PlatilloVistaAdapter.FoodViewHolder>() {

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

        // Cargar imagen desde URL si hay, si no usa placeholder
        Glide.with(holder.image.context)
            .load(item.fotoUrl)
            .placeholder(R.drawable.ic_home) // Asegúrate de tener un placeholder en drawable
            .error(R.drawable.ic_cancel)
            .into(holder.image)

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
            onFavoriteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
