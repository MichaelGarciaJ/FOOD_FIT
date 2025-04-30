package com.mariana.foodfit.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityProfileBinding
import com.mariana.foodfit.ui.auth.LoginActivity
import com.mariana.foodfit.utils.GoogleSignInHelper
import com.mariana.foodfit.utils.ToolbarUtils
import kotlinx.coroutines.launch

/**
 * Activity  que representa el perfil del usuario dentro de la aplicación
 */
class ProfileActivity : AppCompatActivity() {

    // ViewBinding para acceder a los elementos de activity_login.xml
    private lateinit var binding: ActivityProfileBinding

    // Instancia del usuario service
    private val usuarioService = UsuarioService()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prefs: SharedPreferences

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica el tema claro u oscuro según las pregerencias guardadas del usuario.
        aplicarPreferenciasDeTema()

        super.onCreate(savedInstanceState)

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

        mostrarNombreFotoYCorreoUsuario()

        configurarBotonTema()
        configurarBotonLogout()

        configurarBotonEditarPerfil()
    }

    /**
     * Método que aplica el modo claro u oscuro según las preferencias del usuario
     * almacenadas en SharedPreferences bajo la clave "dark_mode".
     */
    private fun aplicarPreferenciasDeTema() {
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    /**
     * Método que recupera los datos del usuario desde el servicio y muestra su nombre,
     * correo e imagen de perfil en la interfaz.
     * */
    private fun mostrarNombreFotoYCorreoUsuario() {
        val textoNombre = binding.profileTvName
        val imagenPerfil = binding.profileIvAvatar
        val correo = binding.profileTvEmail


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
                }
            }
        }
    }

    /**
     * Método qye configura el botón de editar perfil para que abra la actividad
     * EditProfileActivity y cierre la actual.
     */
    private fun configurarBotonEditarPerfil() {
        binding.profileBtnEdit.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Método que configura el botón para cambiar entre modo claro y oscuro.
     * Guarda el nuevo estado del tema en SharedPreferences y cambia el ícono del botón dependiendo del modo actual.
     */
    private fun configurarBotonTema() {
        binding.profileBtnChangeTheme.setOnClickListener {
            val currentlyDark = prefs.getBoolean("dark_mode", false)
            val nuevoModoOscuro = !currentlyDark

            // Guardar el nuevo estado
            prefs.edit().putBoolean("dark_mode", !currentlyDark).apply()

            // Cambiar el tema
            AppCompatDelegate.setDefaultNightMode(
                if (nuevoModoOscuro) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            // Cambiar el ícono del botón según el nuevo modo
            val nuevoIcono =
                if (nuevoModoOscuro) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
            binding.profileBtnChangeTheme.setImageResource(nuevoIcono)
        }

        // Establecer el ícono adecuado al iniciar
        val currentlyDark = prefs.getBoolean("dark_mode", false)
        val iconoInicial = if (currentlyDark) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        binding.profileBtnChangeTheme.setImageResource(iconoInicial)
    }

    /**
     * Método que configura el botón de logout para cerrar la sesión del usuario.
     * Finalmente redirige a LoginActivity y finaliza la actual.
     */
    private fun configurarBotonLogout() {
        binding.profileBtnLogout.setOnClickListener {
            usuarioService.logout()

            if (prefs.getBoolean("is_google_login", false)) {
                googleSignInClient.signOut()
            }

            prefs.edit().remove("is_google_login").apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
