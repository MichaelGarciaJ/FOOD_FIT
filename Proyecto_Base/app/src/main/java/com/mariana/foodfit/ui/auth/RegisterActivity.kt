package com.mariana.foodfit.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityRegisterBinding
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch

/**
 * Activity que maneja el proceso de registro de nuevos usuarios.
 */
class RegisterActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_register.xml
    private lateinit var binding: ActivityRegisterBinding

    // Instancia del usuario service
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
            binding.registerBtRegister.text = "Registrando..."


            // Obtener valores de los campos de texto
            val nombre = binding.registerEtNombre.text.toString().trim()
            val correo = binding.registerEtCorreoElectronico.text.toString().trim()
            val contrasena = binding.registerEtContrasenya.text.toString().trim()
            val confirmarContrasena = binding.registerEtConfirmarContrasenya.text.toString().trim()

            // Validaciones
            if (!Utils.validarNombreUsuario(nombre)) {
                Utils.mostrarMensaje(this, "Nombre inválido. Usa 3-20 caracteres")
                resetRegisterButton()
                return@setOnClickListener
            }

            if (!Utils.comprobarCorreo(correo)) {
                Utils.mostrarMensaje(this, "Correo inválido")
                resetRegisterButton()
                return@setOnClickListener
            }

            if (contrasena.length < 6) {
                Utils.mostrarMensaje(this, "La contraseña debe tener al menos 6 caracteres")
                resetRegisterButton()
                return@setOnClickListener
            }

            if (contrasena != confirmarContrasena) {
                Utils.mostrarMensaje(this, "Las contraseñas no coinciden")
                resetRegisterButton()
                return@setOnClickListener
            }

            // Registro del usuario con corrutinas
            lifecycleScope.launch {
                try {
                    val usuario = usuarioService.register(nombre, correo, contrasena)
                    if (usuario != null) {
                        Utils.mostrarMensaje(this@RegisterActivity, "Registro exitoso")
                        finish()
                    } else {
                        Utils.mostrarMensaje(this@RegisterActivity, "No se pudo crear el usuario")
                    }

                } catch (e: Exception) {
                    Utils.mostrarMensaje(this@RegisterActivity, "Error: ${e.message}")
                } catch (e: FirebaseAuthUserCollisionException) {
                    Utils.mostrarMensaje(this@RegisterActivity, "Este correo ya está registrado")
                } finally {
                    // Rehabilitar el botón después de completar la operación.
                    runOnUiThread { resetRegisterButton() }
                }
            }
        }
    }

    /**
     * Método que restaura el botón de guardar a su estado original.
     */
    private fun resetRegisterButton() {
        binding.registerBtRegister.text = getString(R.string.register_bt_registrarse)
        binding.registerBtRegister.isEnabled = true
    }

}