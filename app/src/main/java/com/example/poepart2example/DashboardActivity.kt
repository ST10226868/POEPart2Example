package com.example.poepart2example

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve user's tracked time, tasks, etc. from Firestore
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Query to retrieve total hours worked
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val totalHoursTextView: TextView = findViewById(R.id.text_total_hours)
                    val totalHours = document.getDouble("total_hours") ?: 0.0
                    totalHoursTextView.text = "Total Hours Worked: $totalHours"
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }

            val projectsTasksTextView: TextView = findViewById(R.id.text_projects_tasks)
            val projectsTasksList = mutableListOf<String>()

            firestore.collection("projects_tasks").whereEqualTo("userId", userId).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val projectTask = document.getString("name") ?: ""
                        projectsTasksList.add(projectTask)
                    }

                    // Display the list of projects/tasks assigned
                    val projectsTasksText = projectsTasksList.joinToString(", ")
                    projectsTasksTextView.text = "Projects/Tasks Assigned: $projectsTasksText"
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }
}
