package com.example.poepart2example.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.poepart2example.CategoryActivity
import com.example.poepart2example.R
import com.example.poepart2example.TimesheetActivity
import com.example.poepart2example.ViewEntriesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask


class DashboardFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        val textViewDate: TextView = view.findViewById(R.id.text_view_date)
        val textViewTime: TextView = view.findViewById(R.id.text_view_time)
        val buttonCategory: Button = view.findViewById(R.id.button_category)
        val buttonTimesheet: Button = view.findViewById(R.id.button_timesheet)
        val buttonViewRecords: Button = view.findViewById(R.id.button_view_record)

        // Get the current user's ID
        val userId = auth.currentUser?.uid

        // Display current date
        val currentDate =
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        textViewDate.text = currentDate

        // Update time every second
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    val currentTime =
                        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                    textViewTime.text = currentTime
                }
            }
        }, 0, 1000)

        // Get the first name and last name from Firestore and display the personalized welcome message
        userId?.let { uid ->
            firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")
                    val welcomeMessage = "Welcome $firstName $lastName"
                    view.findViewById<TextView>(R.id.text_view_welcome_message).text =
                        welcomeMessage
                }
            }
        }

        // Button click listeners
        buttonCategory.setOnClickListener {
            // Open Category Activity
            startActivity(Intent(requireContext(), CategoryActivity::class.java))
        }

        buttonTimesheet.setOnClickListener {
            // Open Timesheet Activity
            startActivity(Intent(requireContext(), TimesheetActivity::class.java))
        }

        buttonViewRecords.setOnClickListener {
            // Open Timesheet Activity
            startActivity(Intent(requireContext(), ViewEntriesActivity::class.java))
        }

        return view
    }
}
