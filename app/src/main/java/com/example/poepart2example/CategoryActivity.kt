package com.example.poepart2example

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CategoryActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val editTextCategory: EditText = findViewById(R.id.edit_text_category)
        val saveButton: Button = findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            val category = editTextCategory.text.toString().trim()

            if (category.isNotEmpty()) {
                saveCategoryToFirestore(category)
            } else {
                Toast.makeText(this, "Please enter a category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCategoryToFirestore(category: String) {
        // Add category to Firestore
        db.collection("categories")
            .add(mapOf("name" to category))
            .addOnSuccessListener {
                Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show()
                // Redirect user back to Dashboard
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save category: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
