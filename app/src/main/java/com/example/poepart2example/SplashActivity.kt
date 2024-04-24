package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_SCREEN_DURATION: Long = 2000 // Splash screen duration: 2000 milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for a certain duration and then transition to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextActivity()
        }, SPLASH_SCREEN_DURATION)
    }

    private fun navigateToNextActivity() {
        // Start the LoginActivity and finish this splash screen activity
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
