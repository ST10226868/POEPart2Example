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
        val startDatePicker: DatePicker = findViewById(R.id.datePicker_start)
        val startTimePicker: TimePicker = findViewById(R.id.timePicker_start)
        val endDatePicker: DatePicker = findViewById(R.id.datePicker_end)
        val endTimePicker: TimePicker = findViewById(R.id.timePicker_end)
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
        val currentDate = Calendar.getInstance()
        startDatePicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
        endDatePicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))

        // Set current time as default start time and end time
        startTimePicker.currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
        startTimePicker.currentMinute = currentDate.get(Calendar.MINUTE)
        endTimePicker.currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
        endTimePicker.currentMinute = currentDate.get(Calendar.MINUTE)

        // Add photo button click listener
        addPhotoButton.setOnClickListener {
            openImagePicker()
        }

        // Save button click listener
        saveButton.setOnClickListener {
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString().trim()
            val startDate = "${startDatePicker.month + 1}/${startDatePicker.dayOfMonth}/${startDatePicker.year}"
            val startTime = "${startTimePicker.currentHour}:${String.format("%02d", startTimePicker.currentMinute)}"
            val endDate = "${endDatePicker.month + 1}/${endDatePicker.dayOfMonth}/${endDatePicker.year}"
            val endTime = "${endTimePicker.currentHour}:${String.format("%02d", endTimePicker.currentMinute)}"
            val minGoal = minGoalEditText.text.toString().trim()
            val maxGoal = maxGoalEditText.text.toString().trim()

            if (category.isNotEmpty() && description.isNotEmpty() && startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()  && minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                saveTimesheetToFirestore(category, description, startDate, startTime, endDate, endTime, minGoal, maxGoal)
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
