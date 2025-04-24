package com.mariana.foodfit.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityRegisterBinding
import com.mariana.foodfit.utils.Utils.Companion.comprobarCorreo
import com.mariana.foodfit.utils.Utils.Companion.mostrarMensaje
import com.mariana.foodfit.utils.Utils.Companion.validarNombreUsuario
import kotlinx.coroutines.launch

/**
 * Activity que maneja el proceso de registro de nuevos usuarios.
 */
class RegisterActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_login.xml
    private lateinit var binding: ActivityRegisterBinding
    // Instancia del service
    private val usuarioService = UsuarioService()

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Configurar el botón para volver al login
        cancelAndGoToLogin()

        // Configurar el botón de registro
        createUser()
    }

    /**
     * Método que configura el listener para el texto "Iniciar sesión" que permite
     * volver a la última pantalla que no se ha finalizado (activity_login.xml) cancelando el registro.
     *
     * Deshabilita temporalmente el botón para evitar múltiples clics.
     */
    private fun cancelAndGoToLogin() {
        binding.registerTvLoginNow.setOnClickListener {
            binding.registerTvLoginNow.isEnabled = false
            finish();
        }
    }

    /**
     * Método que maneja el proceso completo de registro de un nuevo usuario.
     */
    private fun createUser() {
        binding.registerBtRegister.setOnClickListener {
            binding.registerBtRegister.isEnabled = false

            // Obtener valores de los campos de texto
            val nombre = binding.registerEtNombre.text.toString().trim()
            val correo = binding.registerEtCorreoElectronico.text.toString().trim()
            val contrasena = binding.registerEtContrasenya.text.toString().trim()
            val confirmarContrasena = binding.registerEtConfirmarContrasenya.text.toString().trim()

            // Validaciones
            if (!validarNombreUsuario(nombre)) {
                mostrarMensaje(this, "Nombre inválido. Usa 3-20 caracteres")
                binding.registerBtRegister.isEnabled = true
                return@setOnClickListener
            }

            if (!comprobarCorreo(correo)) {
                mostrarMensaje(this, "Correo inválido")
                binding.registerBtRegister.isEnabled = true
                return@setOnClickListener
            }

            if (contrasena.length < 6) {
                mostrarMensaje(this, "La contraseña debe tener al menos 6 caracteres")
                binding.registerBtRegister.isEnabled = true
                return@setOnClickListener
            }

            if (contrasena != confirmarContrasena) {
                mostrarMensaje(this, "Las contraseñas no coinciden")
                binding.registerBtRegister.isEnabled = true
                return@setOnClickListener
            }

            // Registro del usuario con corrutinas
            lifecycleScope.launch {
                try {
                    val usuario = usuarioService.register(nombre, correo, contrasena)
                    if (usuario != null) {
                        mostrarMensaje(this@RegisterActivity, "Registro exitoso")
                        finish()
                    } else {
                        mostrarMensaje(this@RegisterActivity, "No se pudo crear el usuario")
                    }
                } catch (e: Exception) {
                    mostrarMensaje(this@RegisterActivity, "Error: ${e.message}")
                } finally {
                    // Rehabilitar el botón después de completar la operación.
                    runOnUiThread {binding.registerBtRegister.isEnabled = true}
                }
            }
        }
    }

}