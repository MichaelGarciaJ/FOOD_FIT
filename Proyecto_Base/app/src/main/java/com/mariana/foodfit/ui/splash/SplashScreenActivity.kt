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

class SplashScreenActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Cargar animaciones
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Aplicar animaciones al logo
        val splashIvLogo: ImageView = findViewById(R.id.splash_iv_logo)
        splashIvLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, LoginActivity:: class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        },1500)
    }

}