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
import com.mariana.foodfit.ui.home.HomeActivity
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
                    // Solo abre HomeActivity si no estamos ya en ella
                    if (requireActivity() !is HomeActivity) {
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else {
                        Utils.mostrarMensaje(context, "Ya estas en Home")
                    }
                }

                R.id.menuHomeDesayuno -> {
                    Utils.mostrarMensaje(context, "Desayuno")
                }

                R.id.menuHomeComida -> {
                    Utils.mostrarMensaje(context, "Comida")
                }

                R.id.menuHomeCena -> {
                    Utils.mostrarMensaje(context, "Cena")
                }

                R.id.menuHomeSnack -> {
                    Utils.mostrarMensaje(context, "Snack")
                }

                R.id.menuHomeCrear -> {
                    Utils.mostrarMensaje(context, "Crear nuevo")
                }

                R.id.menuHomeFavoritos -> {
                    Utils.mostrarMensaje(context, "Favoritos")
                }

                R.id.menuHomeMenu -> {
                    Utils.mostrarMensaje(context, "Menú semanal")
                }

                R.id.menuHomePerfil -> {
                    // Solo abre ProfileActivity si no estamos ya en ella
                    if (requireActivity() !is ProfileActivity) {
                        val intent = Intent(requireActivity(), ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    } else {
                        Utils.mostrarMensaje(context, "Ya estas en Perfil")
                    }
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
