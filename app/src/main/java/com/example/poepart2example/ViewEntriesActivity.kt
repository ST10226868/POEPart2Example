package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

class ViewEntriesActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_entries)

        val listView: ListView = findViewById(R.id.entries_list_view)

        // Query Firestore for timesheet entries
        db.collection("timesheet_entries")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val entries = mutableListOf<String>()
                for (document in documents.documents) {
                    val entryText = "${document.getString("category")}- ${document.getString("description")} - ${document.getString("startDate")} ${document.getString("startTime")} \n ${document.getString("endDate")}- ${document.getString("endTime")}"
                    entries.add(entryText)
                }

                // Display entries in ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
                listView.adapter = adapter

                // ListView item click listener
                listView.setOnItemClickListener { parent, view, position, id ->
                    // Retrieve document ID of selected entry
                    val selectedEntryId = documents.documents[position].id
                    // Use the document ID to retrieve the photo for this entry, if it exists
                    retrievePhoto(selectedEntryId)
                }
            }
            .addOnFailureListener { e ->
                // Handle errors
                Toast.makeText(this, "Failed to retrieve timesheet entries: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun retrievePhoto(entryId: String) {
        val storageRef = storage.reference
        val photoRef = storageRef.child("photos").child("$entryId.jpg")

        photoRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Photo URL retrieved successfully, open it or display it in an ImageView
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                // Handle errors
                Toast.makeText(this, "Failed to retrieve photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}