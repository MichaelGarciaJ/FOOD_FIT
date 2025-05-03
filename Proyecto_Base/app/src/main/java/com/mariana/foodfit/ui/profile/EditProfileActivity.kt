package com.mariana.foodfit.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityEditProfileBinding
import com.mariana.foodfit.utils.ToolbarUtils
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Activity que permite al usuario editar su perfil, incluyendo nombre, foto y contraseña.
 */
class EditProfileActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_edit_profile.xml
    private lateinit var binding: ActivityEditProfileBinding

    // Instancia del usuario service
    private val usuarioService = UsuarioService()

    // Variable para almacenar temporalmente la nueva foto seleccionada (aún no guardada)
    private var nuevaFotoUri: Uri? = null

    // Lanzador de actividad para seleccionar una imagen del dispositivo
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                mostrarFotoSeleccionada(it)
            }
        }

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando ViewBinding
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Configura el botón del menú (hamburguesa) y el DrawerLayout del toolbar personalizado
        ToolbarUtils.configurarDrawerToggle(
            binding.editProfileCustomToolbar,
            binding.editProfileDrawerLayout
        )

        // Configura los distintos elementos de la interfaz
        deshabilitarPasswordGoogle()
        mostrarDatosActualesUsuario()
        configurarBotonCamara()
        guardarCambios()
        goToProfileActivityCancel()
    }

    /**
     * Método que configura el botón para seleccionar una nueva foto desde la galería.
     */
    private fun configurarBotonCamara() {
        binding.editProfileBtnChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    /**
     * Método que configura el botón de cancelar para volver al perfil sin guardar cambios.
     */
    private fun goToProfileActivityCancel() {
        binding.editProfileBtnCancel.setOnClickListener {
            binding.editProfileBtnCancel.isEnabled = false
            val intent = Intent(this@EditProfileActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Método que muestra en pantalla la foto seleccionada (no la guarda aún).
     *
     * @param uri URI de la imagen seleccionada.
     */
    private fun mostrarFotoSeleccionada(uri: Uri) {
        nuevaFotoUri = uri

        // Solo mostramos en pantalla, no guardamos aún
        Glide.with(this@EditProfileActivity)
            .load(uri)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .circleCrop()
            .into(binding.editProfileIvAvatar)

        Utils.mostrarMensaje(
            this@EditProfileActivity,
            "Foto seleccionada. Guarda para aplicar cambios", Toast.LENGTH_LONG
        )
    }

    /**
     * Método que recupera los datos actuales del usuario y los muestra en la interfaz (nombre e imagen).
     */
    private fun mostrarDatosActualesUsuario() {
        val imagenPerfil = binding.editProfileIvAvatar

        // Lanza una corrutina para obtener el usuario de forma asíncrona
        lifecycleScope.launch {
            val usuario = usuarioService.getCurrentUser()

            usuario?.let {
                binding.editProfileEtName.setText(it.nombre)

                // Si hay una URL de imagen, la cargamos con Glide
                if (!it.photoUrl.isNullOrBlank()) {
                    Glide.with(this@EditProfileActivity)
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
     * Método que maneja el proceso completo de guardar los cambios del perfil (nombre, foto y contraseña).
     */
    private fun guardarCambios() {
        binding.editProfileBtnSave.setOnClickListener {
            binding.editProfileBtnSave.isEnabled = false
            binding.editProfileBtnSave.text = "Guardando..."

            val nuevoNombre = binding.editProfileEtName.text.toString().trim()
            val nuevaPassword = binding.editProfileEtNewPassword.text.toString().trim()
            val currentPassword = binding.editProfileEtCurrentPassword.text.toString().trim()
            val repetirPassword = binding.editProfileEtRepeatPassword.text.toString().trim()
            val hayNuevaFoto = nuevaFotoUri != null


            // Validación de entrada básica
            if (!validarEntradas(nuevoNombre, nuevaPassword, repetirPassword)) {
                resetSaveButton()
                return@setOnClickListener
            }

            val quiereCambiarPassword = nuevaPassword.isNotEmpty() || repetirPassword.isNotEmpty()

            // Lanza una corrutina para obtener el usuario de forma asíncrona
            lifecycleScope.launch {
                val authUser = FirebaseAuth.getInstance().currentUser
                val usuarioActual = usuarioService.getCurrentUser()

                if (usuarioActual == null || authUser == null) {
                    Utils.mostrarMensaje(
                        this@EditProfileActivity,
                        "No se pudo obtener el usuario actual"
                    )
                    resetSaveButton()
                    return@launch
                }

                val nombreCambio = nuevoNombre != usuarioActual.nombre

                if (!nombreCambio && !quiereCambiarPassword && !hayNuevaFoto) {
                    Utils.mostrarMensaje(
                        this@EditProfileActivity,
                        "No has realizado ningún cambio"
                    )
                    resetSaveButton()
                    return@launch
                }

                try {
                    if (nombreCambio) {
                        actualizarNombre(authUser, nuevoNombre)
                    }

                    if (quiereCambiarPassword) {
                        actualizarPassword(authUser, currentPassword, nuevaPassword)
                    }

                    if (hayNuevaFoto) {
                        actualizarFotoPerfil(authUser, nuevaFotoUri!!)
                    }

                    val usuarioActualizado = usuarioActual.copy(
                        nombre = nuevoNombre,
                        photoUrl = nuevaFotoUri?.toString() ?: usuarioActual.photoUrl
                    )

                    usuarioService.update(usuarioActualizado)

                    Utils.mostrarMensaje(
                        this@EditProfileActivity,
                        "Datos actualizados correctamente"
                    )

                    startActivity(Intent(this@EditProfileActivity, ProfileActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Utils.mostrarMensaje(
                        this@EditProfileActivity,
                        "Error al actualizar: ${e.message}"
                    )
                } finally {
                    runOnUiThread { resetSaveButton() }
                }
            }
        }
    }

    /**
     * Método que desactiva los campos de contraseña si el usuario inició sesión con Google.
     */
    private fun deshabilitarPasswordGoogle() {
        val authUser = FirebaseAuth.getInstance().currentUser
        val esGoogleUser = authUser?.providerData?.any { it.providerId == "google.com" } == true

        if (esGoogleUser) {
            binding.editProfileEtCurrentPassword.isEnabled = false
            binding.editProfileEtNewPassword.isEnabled = false
            binding.editProfileEtRepeatPassword.isEnabled = false

            binding.editProfileCurrentPasswordLayout.hint = null
            binding.editProfileNewPasswordLayout.hint = null
            binding.editProfileRepeatPasswordLayout.hint = null

            binding.editProfileCurrentPasswordLayout.hint = "Cuenta Google"
            binding.editProfileNewPasswordLayout.hint = "Cuenta Google"
            binding.editProfileRepeatPasswordLayout.hint = "Cuenta Google"
            binding.editProfileCurrentPasswordLayout.invalidate()
            binding.editProfileNewPasswordLayout.invalidate()
            binding.editProfileRepeatPasswordLayout.invalidate()
        }
    }

    /**
     * Método que valida las entradas del formulario (nombre y contraseñas).
     *
     * @param nombre Nuevo nombre ingresado.
     * @param nuevaPassword Nueva contraseña ingresada.
     * @param repetirPassword Repetición de la nueva contraseña.
     * @return true si las entradas son válidas; false si hay errores.
     */
    private fun validarEntradas(
        nombre: String,
        nuevaPassword: String,
        repetirPassword: String
    ): Boolean {
        if (!Utils.validarNombreUsuario(nombre)) {
            Utils.mostrarMensaje(this, "Nombre inválido. Usa 3-20 caracteres")
            return false
        }

        if (nuevaPassword.isNotEmpty() || repetirPassword.isNotEmpty()) {
            if (nuevaPassword.length < 6) {
                Utils.mostrarMensaje(this, "La contraseña debe tener al menos 6 caracteres")
                return false
            }
            if (nuevaPassword != repetirPassword) {
                Utils.mostrarMensaje(this, "Las contraseñas no coinciden")
                return false
            }
        }
        return true
    }

    /**
     * Método que actualiza la foto de perfil en Firebase Authentication.
     *
     * @param authUser Usuario autenticado.
     * @param uri URI de la nueva imagen.
     */

    private suspend fun actualizarFotoPerfil(authUser: FirebaseUser, uri: Uri) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        authUser.updateProfile(profileUpdates).await()
    }

    /**
     * Método que actualiza el nombre de perfil en Firebase Authentication.
     *
     * @param authUser Usuario autenticado.
     * @param nuevoNombre Nuevo nombre a establecer.
     */
    private suspend fun actualizarNombre(authUser: FirebaseUser, nuevoNombre: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = nuevoNombre
        }
        authUser.updateProfile(profileUpdates).await()
    }

    /**
     * Método que actualiza la contraseña del usuario después de reautenticarlo.
     *
     * @param authUser Usuario autenticado.
     * @param currentPassword Contraseña actual.
     * @param nuevaPassword Nueva contraseña.
     */
    private suspend fun actualizarPassword(
        authUser: FirebaseUser,
        currentPassword: String,
        nuevaPassword: String
    ) {
        if (nuevaPassword == currentPassword) {
            Utils.mostrarMensaje(
                this@EditProfileActivity,
                "La nueva contraseña no puede ser igual a la actual"
            )
            throw IllegalStateException("Contraseña igual a la actual")
        }

        val credential = EmailAuthProvider.getCredential(authUser.email!!, currentPassword)

        try {
            authUser.reauthenticate(credential).await()
        } catch (e: Exception) {
            Utils.mostrarMensaje(
                this@EditProfileActivity,
                "La contraseña actual es incorrecta: ${e.message}"
            )
            throw e
        }

        authUser.updatePassword(nuevaPassword).await()
    }

    /**
     * Método que restaura el botón de guardar a su estado original.
     */
    private fun resetSaveButton() {
        binding.editProfileBtnSave.text = getString(R.string.editProfileBtnSave)
        binding.editProfileBtnSave.isEnabled = true
    }

}