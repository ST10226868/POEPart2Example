package com.example.tictrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CreateCategory : AppCompatActivity() {
    private lateinit var edtCategoryName: EditText
    private lateinit var edtCategoryDescription: EditText
    private lateinit var btnAddCategory: Button
    private val firestoreDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        edtCategoryName = findViewById(R.id.edtcategoryname)
        edtCategoryDescription = findViewById(R.id.edtcategorydescription)
        btnAddCategory = findViewById(R.id.btnAddCategory)

        btnAddCategory.setOnClickListener {
            val categoryName = edtCategoryName.text.toString().trim()
            val categoryDescription = edtCategoryDescription.text.toString().trim()

            // Check if category name and description are not empty
            if (categoryName.isEmpty()) {
                // Display error message for category name
                edtCategoryName.error = "Please enter category name"
            } else if (categoryDescription.isEmpty()) {
                // Display error message for category description
                edtCategoryDescription.error = "Please enter category description"
            } else {
                // Create a new category object
                val category = hashMapOf(
                    "name" to categoryName,
                    "description" to categoryDescription
                )

                // Add the category to Firestore
                firestoreDB.collection("Categories")
                    .add(category)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                        edtCategoryName.text.clear()
                        edtCategoryDescription.text.clear()
                        startActivity(Intent(this@CreateCategory, HomePage::class.java))
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Couldn't add category. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
