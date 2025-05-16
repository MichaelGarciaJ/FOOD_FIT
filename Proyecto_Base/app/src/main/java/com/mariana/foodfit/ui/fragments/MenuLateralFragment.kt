package com.mariana.foodfit.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.FragmentMenuLateralBinding
import com.mariana.foodfit.ui.favorite.FavoriteActivity
import com.mariana.foodfit.ui.home.HomeActivity
import com.mariana.foodfit.ui.meals.breakfast.BreakfastActivity
import com.mariana.foodfit.ui.meals.createFoodPlate.MyFoodPlateActivity
import com.mariana.foodfit.ui.meals.dinner.DinnerActivity
import com.mariana.foodfit.ui.meals.lunch.LunchActivity
import com.mariana.foodfit.ui.meals.snack.SnackActivity
import com.mariana.foodfit.ui.profile.ProfileActivity
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

/**
 * Fragment que representa el menú lateral de navegación.
 */
class MenuLateralFragment : Fragment() {

    // ViewBinding para acceder a los elementos de fragment_menu_lateral.xml
    private var _binding: FragmentMenuLateralBinding? = null

    // Se asegura de que no sea null al acceder.
    private val binding get() = _binding!!

    /**
     * Método que infla el XML del fragmento y guarda el binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuLateralBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Método que llama a configurar el menú lateral.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mostrarNombreUsuarioYFotoUsuario()
        configurarMenu()
    }

    /**
     * Método que configura el comportamiento de los ítems del menú lateral.
     */
    private fun configurarMenu() {
        binding.fragmentMenuLateral.setNavigationItemSelectedListener { menuItem ->

            // Deshabilita el menú para evitar múltiples clics
            binding.fragmentMenuLateral.menu.setGroupEnabled(0, false)

            when (menuItem.itemId) {
                R.id.menuHome -> {
                    navigateToActivity(HomeActivity::class.java)
                }

                R.id.menuHomeDesayuno -> {
                    navigateToActivity(BreakfastActivity::class.java)
                }

                R.id.menuHomeComida -> {
                    navigateToActivity(LunchActivity::class.java)
                }

                R.id.menuHomeCena -> {
                    navigateToActivity(DinnerActivity::class.java)
                }

                R.id.menuHomeSnack -> {
                    navigateToActivity(SnackActivity::class.java)
                }

                R.id.menuHomeCrear -> {
                    navigateToActivity(MyFoodPlateActivity::class.java)
                }

                R.id.menuHomeFavoritos -> {
                    navigateToActivity(FavoriteActivity::class.java)
                }

                R.id.menuHomePerfil -> {
                    navigateToActivity(ProfileActivity::class.java)
                }
            }
            // Rehabilita el menú después de 500ms
            binding.fragmentMenuLateral.postDelayed({
                _binding?.fragmentMenuLateral?.menu?.setGroupEnabled(0, true)
            }, 500)

            true
        }
    }

    /**
     * Método que maneja la navegación hacia otra Activity. Si ya estamos en ella, muestra un mensaje.
     *
     * @param targetActivity La clase de la Activity de destino.
     */
    private fun navigateToActivity(targetActivity: Class<*>) {
        // Solo abre la activity si no estamos em ella.
        if (requireActivity()::class.java != targetActivity) {
            val intent = Intent(requireContext(), targetActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            Utils.mostrarMensaje(context, "Ya estás en esta sección")
        }
    }

    /**
     * Método que carga y muestra la información del usuario actual (nombre y foto de perfil)
     * en el header del menú lateral.
     */
    private fun mostrarNombreUsuarioYFotoUsuario() {
        val headerView = binding.fragmentMenuLateral.getHeaderView(0)
        val textoNombre = headerView.findViewById<TextView>(R.id.menuTvHeaderTvPerfil)
        val imagenPerfil = headerView.findViewById<ImageView>(R.id.menuIwHeaderIbPerfil)

        // Lanza una corrutina para obtener el usuario de forma asíncrona
        lifecycleScope.launch {
            val usuarioService = UsuarioService()
            val usuario = usuarioService.getCurrentUser()

            usuario?.let {
                textoNombre.text = it.nombre

                // Si hay una URL de imagen, la cargamos con Glide
                if (!it.photoUrl.isNullOrBlank()) {
                    Glide.with(requireContext())
                        .load(it.photoUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person) // si falla, muestra el ícono por defecto
                        .circleCrop()
                        .into(imagenPerfil)
                } else {
                    imagenPerfil.setImageResource(R.drawable.ic_person)
                }
            }
        }
    }

    /**
     * Método quue limpia el binding para evitar que la vista se quede ocupando memoria luego de destruirse.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding.fragmentMenuLateral.removeCallbacks(null)
        _binding = null
    }
}
