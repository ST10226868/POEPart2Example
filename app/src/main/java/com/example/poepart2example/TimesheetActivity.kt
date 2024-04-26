package com.example.poepart2example

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TimesheetActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        val categorySpinner: Spinner = findViewById(R.id.category_spinner)
        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        val startDateEditText: EditText = findViewById(R.id.edit_text_start_date)
        val startTimeEditText: EditText = findViewById(R.id.edit_text_start_time)
        val startAmPmSpinner: Spinner = findViewById(R.id.spinner_start_ampm)
        val endDateEditText: EditText = findViewById(R.id.edit_text_end_date)
        val endTimeEditText: EditText = findViewById(R.id.edit_text_end_time)
        val endAmPmSpinner: Spinner = findViewById(R.id.spinner_end_ampm)
        val saveButton: Button = findViewById(R.id.save_button)

        // Set up category spinner
        val categories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Load categories from Firestore
        loadCategories(categories, adapter)

        // Set current date as default start date
        val currentDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        startDateEditText.setText(currentDate)

        // Set current time as default start time and end time
        val currentTime = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
        startTimeEditText.setText(currentTime)
        endTimeEditText.setText(currentTime)

        // Set up AM/PM spinner
        val ampmAdapter = ArrayAdapter.createFromResource(this, R.array.ampm_array, android.R.layout.simple_spinner_item)
        ampmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startAmPmSpinner.adapter = ampmAdapter
        endAmPmSpinner.adapter = ampmAdapter

        // Save button click listener
        saveButton.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString().trim()
            val startDate = startDateEditText.text.toString().trim()
            val startTime = "${startTimeEditText.text.toString().trim()} ${startAmPmSpinner.selectedItem}"
            val endDate = endDateEditText.text.toString().trim()
            val endTime = "${endTimeEditText.text.toString().trim()} ${endAmPmSpinner.selectedItem}"

            if (category.isNotEmpty() && description.isNotEmpty() && startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()) {
                saveTimesheetToFirestore(category, description, startDate, startTime, endDate, endTime)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories(categories: MutableList<String>, adapter: ArrayAdapter<String>) {
        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoryName = document.getString("name")
                    if (categoryName != null) {
                        categories.add(categoryName)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load categories: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveTimesheetToFirestore(category: String, description: String, startDate: String, startTime: String, endDate: String, endTime: String) {
        // Add timesheet entry to Firestore
        val entry = hashMapOf(
            "category" to category,
            "description" to description,
            "startDate" to startDate,
            "startTime" to startTime,
            "endDate" to endDate,
            "endTime" to endTime
            // Add more fields as needed
        )

        db.collection("timesheet_entries")
            .add(entry)
            .addOnSuccessListener {
                Toast.makeText(this, "Timesheet entry saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save timesheet entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
