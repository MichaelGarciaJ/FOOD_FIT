import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariana.foodfit.R
import com.mariana.foodfit.data.entity.PlatilloVistaItem

/**
 * Adapter para mostrar una lista de platillos en un RecyclerView.
 *
 * @param items Lista de objetos PlatilloVistaItem que se mostrarán en el RecyclerView.
 * @param onFavoriteClick Función callback que se ejecuta cuando un ícono de favorito es clickeado.
 */
class PlatilloVistaAdapter(
    private val items: List<PlatilloVistaItem>,
    private val onFavoriteClick: (PlatilloVistaItem) -> Unit
) : RecyclerView.Adapter<PlatilloVistaAdapter.FoodViewHolder>() {

    /**
     * ViewHolder que contiene las vistas necesarias para mostrar cada ítem en el RecyclerView.
     *
     * @param itemView Vista completa del ítem (cada platillo).
     */
    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemFoodImage)
        val title: TextView = itemView.findViewById(R.id.itemFoodTitle)
        val subtitle: TextView = itemView.findViewById(R.id.itemFoodSubtitle)
        val favoriteIcon: ImageView = itemView.findViewById(R.id.itemFoodFavoriteIcon)
    }

    /**
     * Método que crea el ViewHolder para cada ítem en el RecyclerView.
     *
     * @param parent El contenedor padre que contiene el RecyclerView.
     * @param viewType El tipo de vista que se está inflando (en este caso, no se usa).
     * @return Un objeto FoodViewHolder con la vista inflada.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    /**
     * Método que asocia los datos del platillo con el ViewHolder.
     *
     * @param holder El FoodViewHolder donde se van a mostrar los datos.
     * @param position La posición del ítem dentro de la lista.
     */
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]

        // Cargar imagen desde URL si hay, si no usa placeholder
        Glide.with(holder.image.context)
            .load(item.fotoUrl)
            .placeholder(R.drawable.ic_food_no) // Asegúrate de tener un placeholder en drawable
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

    /**
     * Método que devuelve el número total de ítems en la lista de platillos.
     *
     * @return El número total de platillos en la lista.
     */
    override fun getItemCount(): Int = items.size

}
