package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        val textViewWelcomeMessage: TextView = findViewById(R.id.text_view_welcome_message)
        val buttonSignOut: Button = findViewById(R.id.button_sign_out)

        // Get the current user's ID
        val userId = auth.currentUser?.uid

        // Get the first name and last name from Firestore and display the personalized welcome message
        userId?.let { uid ->
            firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val welcomeMessage = "Welcome $firstName $lastName"
                    textViewWelcomeMessage.text = welcomeMessage
                }
            }
        }

        // Button click listener for signing out
        buttonSignOut.setOnClickListener {
            // Sign out and navigate to MainActivity
            auth.signOut()
            startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
            finish()
        }
    }
}
