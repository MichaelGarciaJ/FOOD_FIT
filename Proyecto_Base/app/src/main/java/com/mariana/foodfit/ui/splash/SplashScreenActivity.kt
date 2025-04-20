package com.mariana.foodfit.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.mariana.foodfit.ui.auth.LoginActivity
import com.mariana.foodfit.R

/**
 * Activity que muestra una pantalla de inicio (Splash Screen) con animaciones.
 */
class SplashScreenActivity : AppCompatActivity() {

    /**
     * Método llamado al crear la Activity. Configura el layout y las animaciones.
     *
     * @param savedInstanceState Bundle con el estado previo (null si es la primera vez).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Cargar animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Aplicar animaciones al logo
        val splashIvLogo: ImageView = findViewById(R.id.splash_iv_logo)
        splashIvLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1500)
    }

}