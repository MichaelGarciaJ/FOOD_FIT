package com.mariana.foodfit.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.FragmentMenuLateralBinding
import com.mariana.foodfit.ui.profile.ProfileActivity
import com.mariana.foodfit.utils.Utils.Companion.mostrarMensaje

/**
 * Fragment que representa el menú lateral de navegación.
 */
class MenuLateralFragment : Fragment() {

    // ViewBinding para acceder a los elementos de fragment_menu_lateral.xml
    private var _binding: FragmentMenuLateralBinding? = null

    // Se asegura de que no sea null al acceder.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Puedes aquí inicializar algo si lo necesitas antes de crear la vista
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuLateralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarMenu()
    }

    /**
     * Método que configura el comportamiento de los ítems del menú lateral.
     */
    private fun configurarMenu() {
        binding.fragmentMenuLateral.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHomeDesayuno -> {
                    mostrarMensaje(context, "Desayuno")
                }

                R.id.menuHomeComida -> {
                    mostrarMensaje(context, "Comida")
                }

                R.id.menuHomeCena -> {
                    mostrarMensaje(context, "Cena")
                }

                R.id.menuHomeSnack -> {
                    mostrarMensaje(context, "Snack")
                }

                R.id.menuHomeCrear -> {
                    mostrarMensaje(context, "Crear nuevo")
                }

                R.id.menuHomeFavoritos -> {
                    mostrarMensaje(context, "Favoritos")
                }

                R.id.menuHomeMenu -> {
                    mostrarMensaje(context, "Menú semanal")
                }

                R.id.menuHomePerfil -> {
                    // Ir al perfil
                    startActivity(Intent(requireContext(), ProfileActivity::class.java))
                }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
