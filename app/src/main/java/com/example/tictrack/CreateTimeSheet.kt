package com.example.tictrack

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore.Images.Media
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class CreateTimeSheet : AppCompatActivity() {
    private lateinit var spnSelectCategory: Spinner
    private lateinit var txtEnterDescription: EditText
    private lateinit var txtStartDate: EditText
    private lateinit var txtStartTime: EditText
    private lateinit var txtEndDate: EditText
    private lateinit var txtEndTime: EditText
    private lateinit var imageView: ImageView
    private lateinit var btnAddTimeSheet: Button
    private lateinit var btnAddImage: Button
    private lateinit var categoriesList: MutableList<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val storage = FirebaseStorage.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private var photoUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_time_sheet)

        spnSelectCategory = findViewById(R.id.spnSelectCategory)
        txtEnterDescription = findViewById(R.id.txtEnterDescription)
        txtStartDate = findViewById(R.id.txtStartDate)
        txtStartTime = findViewById(R.id.txtStartTime)
        txtEndDate = findViewById(R.id.txtEndDate)
        txtEndTime = findViewById(R.id.txtEndTime)
        imageView = findViewById(R.id.imgAddImage)
        btnAddImage = findViewById(R.id.btnAddImage)


        btnAddTimeSheet = findViewById(R.id.btnAddTimeSheet)
        db = FirebaseFirestore.getInstance()

        categoriesList = mutableListOf()
        categoryAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoriesList)
        spnSelectCategory.adapter = categoryAdapter
        // registerResult()

        // Fetch categories from Firestore and populate Spinner
        fetchCategories()

        btnAddTimeSheet.setOnClickListener {
            addTimeSheetToFirestore()
        }
        txtStartDate.setOnClickListener {
            startDatePicker()
        }


        txtEndDate.setOnClickListener {
            EndDatePicker()
        }
        txtStartTime.setOnClickListener {
            startTimePicker()
        }

        txtEndTime.setOnClickListener {
            EndTimePicker()
        }

        btnAddImage.setOnClickListener {
            openImagePicker()

        }


    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Image captured successfully
                val data: Intent? = result.data
                photoUri = data?.data

                photoUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    imageView.setImageBitmap(bitmap)
                }
            }
        }

    private fun openImagePicker() {
        // Create an intent to capture an image
        val takePictureIntent = Intent(MediaStore.ACTION_PICK_IMAGES)

        // Check if there's a camera activity available to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Launch the camera activity
            imagePickerLauncher.launch(takePictureIntent)
        } else {
            // Show a message or handle the case when no camera activity is available
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // This lambda function is invoked when a time is set
                val formattedTime = "$hourOfDay:$minute"
                txtStartTime.setText(formattedTime)
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
                txtEndTime.setText(formattedTime)
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
                txtStartDate.setText(formattedDate)
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
                txtEndDate.setText(formattedDate)
            },
            2024,
            0,
            15
        )

        datePickerDialog.show()
    }


    private fun fetchCategories() {
        db.collection("Categories")
            .get()
            .addOnSuccessListener { result ->
                categoriesList.clear()
                for (document in result) {
                    val category = document.getString("name")
                    category?.let {
                        categoriesList.add(category)
                    }
                }
                categoryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching categories: $exception", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun addTimeSheetToFirestore() {
        val category = spnSelectCategory.selectedItem.toString()
        val description = txtEnterDescription.text.toString()
        val startDate = txtStartDate.text.toString()
        val startTime = txtStartTime.text.toString()
        val endDate = txtEndDate.text.toString()
        val endTime = txtEndTime.text.toString()


        // Check for empty fields
        if (description.isEmpty()) {
            txtEnterDescription.error = "Description is required!"
            return
        }
        if (startDate.isEmpty()) {
            txtStartDate.error = "Start date is required!"
            return
        }
        if (startTime.isEmpty()) {
            txtStartTime.error = "Start time is required!"
            return
        }
        if (endDate.isEmpty()) {
            txtEndDate.error = "End date is required!"
            return
        }
        if (endTime.isEmpty()) {
            txtEndTime.error = "End time is required!"
            return
        }

        // Create a time sheet object
        val timeSheet = hashMapOf(
            "category" to category,
            "description" to description,
            "startDate" to startDate,
            "startTime" to startTime,
            "endDate" to endDate,
            "endTime" to endTime,

            )

        // Add the time sheet to Firestore
        db.collection("TimeSheets")
            .add(timeSheet)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Time sheet added successfully!", Toast.LENGTH_SHORT).show()

                // adding photo to firebase if not null
                photoUri?.let {

                    uploadPhotoToFirestore(documentReference.id, imageView)
                }
                //finish()
                startActivity(Intent(this@CreateTimeSheet, HomePage::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding time sheet: $e", Toast.LENGTH_SHORT).show()
            }

    }
    private fun uploadPhotoToFirestore(entryId: String, imageView: ImageView) {
        // Get the Bitmap from the ImageView
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap

        // Convert Bitmap to ByteArray
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Upload ByteArray to Firestore
        val storageRef = storage.reference
        val photoRef = storageRef.child("photos").child("$entryId.jpg")

        photoRef.putBytes(imageData)
            .addOnSuccessListener { taskSnapshot ->
                // Photo uploaded successfully
                Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Failed to upload photo
                Toast.makeText(this, "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

