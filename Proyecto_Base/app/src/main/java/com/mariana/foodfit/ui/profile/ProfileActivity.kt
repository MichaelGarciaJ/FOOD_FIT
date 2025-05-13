package com.mariana.foodfit.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.PlatilloService
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityProfileBinding
import com.mariana.foodfit.ui.auth.LoginActivity
import com.mariana.foodfit.utils.GoogleSignInHelper
import com.mariana.foodfit.utils.ThemeUtils
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.launch

/**
 * Activity  que representa el perfil del usuario dentro de la aplicación
 */
class ProfileActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_profile.xml
    private lateinit var binding: ActivityProfileBinding

    // Instancia del usuario service
    private val usuarioService = UsuarioService()

    // Instancia del platillo service
    private val platilloService = PlatilloService()

    // Cliente de autenticación de Google
    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aplica el tema claro u oscuro según las preferencias guardadas
        ThemeUtils.applySavedTheme(this)

        // Inflar el layout usando ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Obtiene una instancia del cliente de inicio de sesión de Google
        googleSignInClient = GoogleSignInHelper.getClient(this@ProfileActivity)

        // Configura el botón del menú (hamburguesa) y el DrawerLayout del toolbar personalizado
        ToolbarUtils.configurarDrawerToggle(
            binding.profileCustomToolbar,
            binding.profileDrawerLayout
        )

        // Ocultar el icono búsqueda del toolbar
        binding.profileCustomToolbar.mostrarBusqueda(false)

        // Configura los distintos elementos de la interfaz
        mostrarNombreFotoYCorreoUsuario()
        mostrarCantidadDeFavoritos()
        configurarBotonTema()
        configurarBotonLogout()
        goToEditProfile()
        actualizarIconoTema()
    }

    /**
     * Método que recupera los datos del usuario desde el servicio y muestra su nombre,
     * correo e imagen de perfil en la interfaz.
     */
    private fun mostrarNombreFotoYCorreoUsuario() {
        val textoNombre = binding.profileTvName
        val imagenPerfil = binding.profileIvAvatar
        val correo = binding.profileTvEmail


        // Lanza una corrutina para obtener el usuario de forma asíncrona
        lifecycleScope.launch {
            val usuario = usuarioService.getCurrentUser()

            usuario?.let {
                textoNombre.text = it.nombre
                correo.text = it.correo

                // Si hay una URL de imagen, la cargamos con Glide
                if (!it.photoUrl.isNullOrBlank()) {
                    Glide.with(this@ProfileActivity)
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
     * Método que recupera la cantidad de favoritos del usuario desde el servicio y muestra su cantidad.
     */
    private fun mostrarCantidadDeFavoritos() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val favoritosIds = platilloService.getFavoritosIds(userId)
            val cantidad = favoritosIds.size

            binding.profileTvFavoritesCount.text = cantidad.toString()
        }
    }

    /**
     * Método qye configura el botón de editar perfil para que abra la actividad
     * EditProfileActivity y cierre la actual.
     */
    private fun goToEditProfile() {
        binding.profileBtnEdit.setOnClickListener {
            binding.profileBtnEdit.isEnabled = false
            val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Método que configura el botón para cambiar entre modo claro, oscuro o seguir sistema.
     * Muestra un diálogo, guarda el nuevo estado en SharedPreferences y cambia el modo.
     */
    private fun configurarBotonTema() {
        binding.profileBtnChangeTheme.setOnClickListener {
            val opciones = arrayOf("Modo claro", "Modo oscuro", "Seguir sistema")
            val currentMode = ThemeUtils.getCurrentMode(this)

            val checkedItem = when (currentMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> 0
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                else -> 2
            }

            AlertDialog.Builder(this)
                .setTitle("Selecciona el tema")
                .setSingleChoiceItems(opciones, checkedItem) { dialog, which ->
                    val selectedMode = when (which) {
                        0 -> AppCompatDelegate.MODE_NIGHT_NO
                        1 -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }

                    ThemeUtils.setThemeMode(this, selectedMode)
                    dialog.dismiss()
                }

                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    /**
     * Método que actualiza el ícono del botón de cambio de tema
     * según el modo de tema actual para sugerir al usuario el próximo modo.
     */
    private fun actualizarIconoTema() {
        val mode = ThemeUtils.getCurrentMode(this)
        val iconRes = when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_dark_mode
            AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_light_mode
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                val currentNightMode =
                    resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                    R.drawable.ic_light_mode  // sistema está en oscuro → mostrar sol (para sugerir claro)
                } else {
                    R.drawable.ic_dark_mode  // sistema está en claro → mostrar luna (para sugerir oscuro)
                }
            }

            else -> R.drawable.ic_dark_mode
        }
        binding.profileBtnChangeTheme.setImageResource(iconRes)
    }


    /**
     * Método que configura el botón de logout para cerrar la sesión del usuario.
     * Si fue un login con Google, también cierra la sesión de Google.
     */
    private fun configurarBotonLogout() {
        binding.profileBtnLogout.setOnClickListener {
            usuarioService.logout()

            val prefs = getSharedPreferences("settings", MODE_PRIVATE)
            if (prefs.getBoolean("is_google_login", false)) {
                googleSignInClient.signOut()
            }

            prefs.edit().remove("is_google_login").apply()

            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}
