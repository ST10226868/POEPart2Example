package com.example.poepart2example

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewEntriesActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val TAG = "ViewEntriesActivity"

    private val allEntries = mutableListOf<Map<String, Any>>()
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_entries)
        val filterByCategory: Spinner = findViewById(R.id.FilterCategorySpinner)
        val listView: ListView = findViewById(R.id.entries_list_view)
        val filterButton: Button = findViewById(R.id.FilterButton)
        val startDatePicker: Button = findViewById(R.id.StartDatePicker)
        val endDatePicker: Button = findViewById(R.id.EndDatepicker)

        // Setting up the category spinner
        val filteredCategories = mutableListOf("All")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filteredCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterByCategory.adapter = adapter

        // Load categories from Firestore
        loadCategories(filteredCategories, adapter)

        // Fetch all entries initially
        fetchAllEntries(listView)

        // Set up the Button click listener for filtering
        filterButton.setOnClickListener {
            val selectedCategory = filterByCategory.selectedItem?.toString()
            filterEntries(selectedCategory, startDate, endDate, listView)
        }

        // Set default text for date pickers
        startDatePicker.text = "Start Date: All"
        endDatePicker.text = "End Date: All"

        // Set up the start date picker
        startDatePicker.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startDatePicker.text = "Start Date: $date"
            }
        }

        // Set up the end date picker
        endDatePicker.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endDatePicker.text = "End Date: $date"
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
                Log.e(TAG, "Error loading categories", exception)
            }
    }

    private fun fetchAllEntries(listView: ListView) {
        db.collection("timesheet_entries").orderBy("startDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    allEntries.add(document.data)
                }
                updateListView(allEntries, listView)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve timesheet entries: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error retrieving timesheet entries", e)
            }
    }

    private fun filterEntries(category: String?, startDate: String?, endDate: String?, listView: ListView) {
        val filteredEntries = allEntries.filter { entry ->
            val entryCategory = entry["category"] as? String
            val entryStartDate = entry["startDate"] as? String

            val categoryMatches = category == null || category == "All" || entryCategory == category
            val dateMatches = isDateInRange(entryStartDate, startDate, endDate)

            categoryMatches && dateMatches
        }
        updateListView(filteredEntries, listView)
    }

    private fun isDateInRange(entryDate: String?, startDate: String?, endDate: String?): Boolean {
        if (entryDate == null) return false

        if (startDate == null && endDate == null) return true

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val entry = dateFormat.parse(entryDate)
        val start = startDate?.let { dateFormat.parse(it) }
        val end = endDate?.let { dateFormat.parse(it) }

        return (start == null || entry >= start) && (end == null || entry <= end)
    }

    private fun updateListView(entries: List<Map<String, Any>>, listView: ListView) {
        val entryTexts = entries.map {
            "${it["category"]} - ${it["description"]} - ${it["startDate"]} ${it["startTime"]} \n ${it["endDate"]} - ${it["endTime"]}"
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entryTexts)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedEntry = entries[position]
            val selectedEntryId = selectedEntry["id"].toString()
            retrievePhoto(selectedEntryId)
        }
    }

    private fun retrievePhoto(entryId: String) {
        val storageRef = storage.reference
        val photoRef = storageRef.child("photos").child("$entryId.jpg")

        photoRef.downloadUrl
            .addOnSuccessListener { uri ->
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No photo available for this entry.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "No photo available for entry $entryId", e)
            }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}