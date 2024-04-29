package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var toggler: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buttons for registering and logging in
        val registerButton: Button = findViewById(R.id.register_button)
        val loginButton: Button = findViewById(R.id.login_button)
        toggler = findViewById(R.id.toggler)

        // Set initial theme based on user preference
        if (isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toggler.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toggler.isChecked = false
        }

        // Set click listeners for the buttons
        registerButton.setOnClickListener {
            // Launch RegisterActivity when the register button is clicked
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            // Launch LoginActivity when the login button is clicked
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // Toggle between light and dark mode
        toggler.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkModeState(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModeState(false)
            }
            recreate()
        }
    }

    // Save dark mode state
    private fun saveDarkModeState(isDarkModeEnabled: Boolean) {
        val sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("darkMode", isDarkModeEnabled)
        editor.apply()
    }

    // Check if dark mode is enabled
    private fun isDarkModeEnabled(): Boolean {
        val sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        return sharedPrefs.getBoolean("darkMode", false)
    }
}
