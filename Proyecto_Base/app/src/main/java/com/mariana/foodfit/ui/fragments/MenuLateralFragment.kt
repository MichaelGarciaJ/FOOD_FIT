package com.mariana.foodfit.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mariana.foodfit.R
import com.mariana.foodfit.databinding.FragmentMenuLateralBinding
import com.mariana.foodfit.ui.profile.ProfileActivity

/**
 * Fragment que representa el menú lateral de navegación.
 */
class MenuLateralFragment : Fragment() {

    // ViewBinding para fragment_menu_lateral.xml
    private var _binding: FragmentMenuLateralBinding? = null
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

    private fun configurarMenu() {
        binding.viewMenuLateral.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHomeDesayuno -> {
                    mostrarMensaje("Desayuno")
                }
                R.id.menuHomeComida -> {
                    mostrarMensaje("Comida")
                }
                R.id.menuHomeCena -> {
                    mostrarMensaje("Cena")
                }
                R.id.menuHomeSnack -> {
                    mostrarMensaje("Snack")
                }
                R.id.menuHomeCrear -> {
                    mostrarMensaje("Crear nuevo")
                }
                R.id.menuHomeFavoritos -> {
                    mostrarMensaje("Favoritos")
                }
                R.id.menuHomeMenu -> {
                    mostrarMensaje("Menú semanal")
                }
                R.id.menuHomePerfil -> {
                    // Ir al perfil
                    startActivity(Intent(requireContext(), ProfileActivity::class.java))
                }
            }
            true
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
