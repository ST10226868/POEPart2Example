package com.example.poepart2example

import android.app.Activity
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

    private var photoUri: Uri? = null

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

        // Add photo button click listener
        addPhotoButton.setOnClickListener {
            openImagePicker()
        }

        // Save button click listener
        saveButton.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString().trim()
            val startDate = startDateEditText.text.toString().trim()
            val startTime = "${startTimeEditText.text.toString().trim()} ${startAmPmSpinner.selectedItem}"
            val endDate = endDateEditText.text.toString().trim()
            val endTime = "${endTimeEditText.text.toString().trim()} ${endAmPmSpinner.selectedItem}"
            val minGoal = minGoalEditText.text.toString().trim()
            val maxGoal = maxGoalEditText.text.toString().trim()

            if (category.isNotEmpty() && description.isNotEmpty() && startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()  && minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                saveTimesheetToFirestore(category, description, startDate, startTime, endDate, endTime ,minGoal,maxGoal)
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
    private fun calculateHourDifference(startTime: String, endTime: String): Float {
        // Define date format
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            // Parse startTime and endTime strings into Date objects
            val startDate = dateFormat.parse(startTime)
            val endDate = dateFormat.parse(endTime)

            // Calculate the difference in milliseconds
            val differenceMillis = endDate.time - startDate.time

            // Convert milliseconds to hours (1 hour = 3600000 milliseconds)
            differenceMillis.toFloat() / 3600000
        } catch (e: Exception) {
            // Handle parsing errors
            e.printStackTrace()
            -1f // Return -1 if an error occurs
        }
    }
}

