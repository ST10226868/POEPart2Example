package com.example.tictrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var btnRedirectSignUp: Button
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // View Binding
        btnRedirectSignUp = findViewById(R.id.btnRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            login()
        }



        val redirectSignUp: Button = findViewById(R.id.btnRedirectSignUp)
        redirectSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }


    }

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        } else {

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    setContentView(R.layout.activity_home_page)
                } else
                    Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
