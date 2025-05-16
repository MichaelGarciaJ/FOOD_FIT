package com.mariana.foodfit.ui.search

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mariana.foodfit.databinding.DialogSearchBinding

/**
 * Diálogo personalizado para realizar una búsqueda de platillos.
 *
 * @property onSearchQuery Lambda que se ejecuta con el texto ingresado cuando se realiza la búsqueda.
 */
class SearchDialog(private val onSearchQuery: (String) -> Unit) : DialogFragment() {

    // Variable privada para el binding, gestionando ciclo de vida
    private var _binding: DialogSearchBinding? = null

    // Getter no nulo para binding, solo válido entre onCreateDialog y onDestroyView
    private val binding get() = _binding!!

    /**
     * Método que crea el diálogo personalizado para la búsqueda.
     *
     * @param savedInstanceState Bundle con estado previo guardado, si existe.
     * @return Dialog objeto del diálogo configurado.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogSearchBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Buscar platillo")
            .setView(binding.root)
            .setPositiveButton("Buscar") { _, _ ->
                val query = binding.searchEt.text.toString().trim()
                if (query.isNotEmpty()) {
                    onSearchQuery(query)
                } else {
                    binding.searchInputLayout.error = "Este campo no puede estar vacío"
                }
            }
            .setNegativeButton("Cancelar", null)

        // Búsqueda desde teclado
        binding.searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEt.text.toString().trim()
                if (query.isNotEmpty()) {
                    onSearchQuery(query)
                    dismiss()
                } else {
                    binding.searchInputLayout.error = "Este campo no puede estar vacío"
                }
                true
            } else {
                false
            }
        }

        return builder.create()
    }

    /**
     * Método que limpia el binding para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
