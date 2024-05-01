package com.example.poepart2example.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.poepart2example.MainActivity
import com.example.poepart2example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        val textViewWelcomeMessage: TextView = view.findViewById(R.id.text_view_welcome_message)
        val buttonSignOut: Button = view.findViewById(R.id.button_sign_out)

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
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}
