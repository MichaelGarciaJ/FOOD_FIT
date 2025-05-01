package com.mariana.foodfit.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.mariana.foodfit.R
import com.mariana.foodfit.data.service.UsuarioService
import com.mariana.foodfit.databinding.ActivityLoginBinding
import com.mariana.foodfit.ui.home.HomeActivity
import com.mariana.foodfit.utils.GoogleSignInHelper
import com.mariana.foodfit.utils.Utils
import kotlinx.coroutines.launch


/**
 * Activity que muestra la pantalla de inicio de sesión (login).
 * Incluye login con email/contraseña y Google Sign-In.
 */
class LoginActivity : AppCompatActivity() {

    // Cliente de autenticación de Google
    private lateinit var googleSignInClient: GoogleSignInClient

    // Código de solicitud utilizado para identificar la respuesta del intent de inicio de sesión con Google
    private val RC_SIGN_IN = 1001

    // ViewBinding para acceder a los elementos de activity_login.xml
    private lateinit var binding: ActivityLoginBinding

    // Instancia del usuario service
    private val usuarioService = UsuarioService()

    /**
     * Método llamado cuando se crea la Activity.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura la verificación de la sesión del usuario.
        verifyActiveSession()

        // Inflar el layout usando ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Establecer el layout inflado como la vista de la actividad
        setContentView(binding.root)

        // Configura el botón para ir a registro.
        goToRegister()

        // Configura el proceso de login
        signIn()

        // Prepara Google Sign-In
        googleSignInClient = GoogleSignInHelper.getClient(this@LoginActivity)

        // Configura login con Google
        startWithGoogle()
    }

    /**
     * Método de ciclo de vida llamado cuando la actividad se reanuda.
     *
     * Rehabilita el botón de registro para evitar estados bloqueados
     * cuando se retorna a esta pantalla.
     */
    override fun onResume() {
        super.onResume()
        // Volver a habilitar el clic cada vez que se muestra la pantalla
        binding.loginTvRegisterNow.isEnabled = true
    }

    /**
     * Método que recibe el resultado de la intent de Google Sign-In.
     * @param requestCode Código de solicitud (RC_SIGN_IN para Google).
     * @param resultCode Código de resultado (no se usa aquí).
     * @param data Intent con los datos de Google Sign-In.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // Obtener la tarea de GoogleSignIn
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Extraer cuenta de Google (lanza ApiException si falla)
                val account = task.getResult(ApiException::class.java)!!

                // Autenticar con Firebase usando el ID token de Google
                lifecycleScope.launch {
                    val usuario = usuarioService.signInWithGoogle(account.idToken!!)
                    if (usuario != null) {
                        // Guardamos el tipo de login en preferencias
                        getSharedPreferences("settings", MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_google_login", true)
                            .apply()

                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Utils.mostrarMensaje(this@LoginActivity, "Error en autenticación Google")
                    }
                    // Rehabilitar el botón de Google al finalizar
                    runOnUiThread { binding.loginBtGoogle.isEnabled = true }
                    binding.loginBtGoogle.text = getString(R.string.login_with_google)
                }

            } catch (e: ApiException) {
                Utils.mostrarMensaje(this, "Google Sign-In falló: ${e.message}")
                binding.loginBtGoogle.isEnabled = true
            }
        }
    }

    /**
     * Método que verifica si existe una sesión de usuario activa, para redirigirte directamente
     * a otra pantalla.
     */
    private fun verifyActiveSession() {
        val usuario = FirebaseAuth.getInstance().currentUser

        if (usuario != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
            return
        }
    }

    /**
     * Método que configura el proceso completo de autenticación.
     */
    private fun signIn() {
        binding.loginBtLogin.setOnClickListener {
            binding.loginBtLogin.isEnabled = false
            binding.loginBtLogin.text = "Cargando..."

            // Obtener valores de los campos de texto
            val correo = binding.loginEtCorreoElectronico.text.toString().trim()
            val contrasena = binding.loginEtContrasenya.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Utils.mostrarMensaje(this, "Correo y contraseña obligatorios")
                binding.loginBtLogin.isEnabled = true
                binding.loginBtLogin.text = getString(R.string.login_bt_login)
                return@setOnClickListener
            }

            // Inicio del usuario con corrutinas
            lifecycleScope.launch {
                try {
                    val usuario = usuarioService.login(correo, contrasena)
                    if (usuario != null) {
                        // Guardamos el tipo de login en preferencias
                        getSharedPreferences("settings", MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_google_login", false)
                            .apply()

                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Utils.mostrarMensaje(this@LoginActivity, "Correo o contraseña incorrectas")
                    }

                } catch (e: Exception) {
                    Utils.mostrarMensaje(this@LoginActivity, "Error: ${e.message}")
                } finally {
                    // Rehabilitar el botón después de completar la operación.
                    binding.loginBtLogin.text = getString(R.string.login_bt_login)
                    runOnUiThread { binding.loginBtLogin.isEnabled = true }
                }
            }
        }
    }

    /**
     * Método que configura el clic del botón de Google para iniciar el flujo de Sign-In.
     * También deshabilita el botón al pulsarlo para evitar dobles lanzamientos.
     */
    private fun startWithGoogle() {
        binding.loginBtGoogle.setOnClickListener {
            binding.loginBtGoogle.isEnabled = false
            binding.loginBtGoogle.text = "Cargando..."
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    /**
     * Método que configura el clic del TextView "¿No tienes cuenta? Regístrate ahora"
     * para navegar a la pantalla de registro (RegisterActivity).
     */
    private fun goToRegister() {
        binding.loginTvRegisterNow.setOnClickListener {
            binding.loginTvRegisterNow.isEnabled = false

            val intent = Intent(
                this@LoginActivity,
                RegisterActivity::class.java
            )
            startActivity(intent)

        }
    }

}