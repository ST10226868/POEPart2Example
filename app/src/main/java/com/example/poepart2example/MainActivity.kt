package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Buttons for registering and logging in
        val registerButton: Button = findViewById(R.id.register_button)
        val loginButton: Button = findViewById(R.id.login_button)

        // Set click listeners for the buttons
        registerButton.setOnClickListener {
            // Launch RegisterActivity when the register button is clicked
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        loginButton.setOnClickListener {
            // Launch LoginActivity when the login button is clicked
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}
