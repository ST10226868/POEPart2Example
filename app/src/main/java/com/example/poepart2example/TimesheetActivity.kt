package com.example.poepart2example

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class TimesheetActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        val categorySpinner: Spinner = findViewById(R.id.category_spinner)
        val descriptionEditText: EditText = findViewById(R.id.edit_text_description)
        startDateEditText = findViewById(R.id.edit_text_start_date)
        endDateEditText = findViewById(R.id.edit_text_end_date)
        startTimeEditText = findViewById(R.id.edit_text_start_time)
        endTimeEditText = findViewById(R.id.edit_text_end_time)
        val minGoalEditText: EditText = findViewById(R.id.edit_text_min_goal)
        val maxGoalEditText: EditText = findViewById(R.id.edit_text_max_goal)
        val addPhotoButton: Button = findViewById(R.id.add_photo_button)
        val saveButton: Button = findViewById(R.id.save_button)

        // Set up category spinner
        val categories = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Load categories from Firestore
        loadCategories(categories, adapter)

        // Set current date as default start date

        // Set up AM/PM spinner

        // Add photo button click listener
        addPhotoButton.setOnClickListener {
            openImagePicker()
        }

        startDateEditText.setOnClickListener {
            startDatePicker()
        }


        endDateEditText.setOnClickListener {
            EndDatePicker()
        }
        startTimeEditText.setOnClickListener {
            startTimePicker()
        }

        endTimeEditText.setOnClickListener {
            EndTimePicker()
        }





        // Save button click listener
        saveButton.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString().trim()
            val startDate = startDateEditText.text.toString().trim()
            val endDate = endDateEditText.text.toString().trim()
            val startTime = startTimeEditText.text.toString().trim()
            val endTime = endTimeEditText.text.toString().trim()
            val minGoal = minGoalEditText.text.toString().trim()
            val maxGoal = maxGoalEditText.text.toString().trim()

            if (category.isNotEmpty() && description.isNotEmpty() && startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()  && minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                saveTimesheetToFirestore(category, description, startDate, startTime, endDate, endTime ,minGoal,maxGoal)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // This lambda function is invoked when a time is set
                val formattedTime = "$hourOfDay:$minute"
                startTimeEditText.setText(formattedTime)
            },
            0,
            0,
            true // If you want the 24-hour view, set this to true, otherwise, set it to false
        )

        timePickerDialog.show()
    }

    private fun EndTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // This lambda function is invoked when a time is set
                val formattedTime = "$hourOfDay:$minute"
                endTimeEditText.setText(formattedTime)
            },
            0,
            0,
            true // If you want the 24-hour view, set this to true, otherwise, set it to false
        )

        timePickerDialog.show()
    }


    private fun startDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // This lambda function is invoked when a date is set
                val formattedDate = "$year.${month + 1}.$dayOfMonth" // Note: month starts from 0
                startDateEditText.setText(formattedDate)
            },
            2024,
            0,
            15
        )

        datePickerDialog.show()
    }

    private fun EndDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // This lambda function is invoked when a date is set
                val formattedDate = "$year.${month + 1}.$dayOfMonth" // Note: month starts from 0
               endDateEditText.setText(formattedDate)
            },
            2024,
            0,
            15
        )

        datePickerDialog.show()
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

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Image selected successfully
            val data: Intent? = result.data
            photoUri = data?.data
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun saveTimesheetToFirestore(category: String, description: String, startDate: String, startTime: String, endDate: String, endTime: String, minGoal: String, maxGoal: String) {
        // Add timesheet entry to Firestore
        val entry = hashMapOf(
            "category" to category,
            "description" to description,
            "startDate" to startDate,
            "startTime" to startTime,
            "endDate" to endDate,
            "endTime" to endTime,
            "minGoal" to minGoal,
            "maxGoal" to maxGoal

        )

        db.collection("timesheet_entries")
            .add(entry)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Timesheet entry saved successfully", Toast.LENGTH_SHORT).show()

                // Upload photo to Firebase Storage if photoUri is not null
                photoUri?.let {
                    uploadPhotoToStorage(documentReference.id, it)
                }

                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save timesheet entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhotoToStorage(entryId: String, uri: Uri) {
        val storageRef = storage.reference
        val photoRef = storageRef.child("photos").child("$entryId.jpg")

        photoRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}