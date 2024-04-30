package com.example.poepart2example

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        val textViewDate: TextView = findViewById(R.id.text_view_date)
        val textViewTime: TextView = findViewById(R.id.text_view_time)
        val buttonCategory: Button = findViewById(R.id.button_category)
        val buttonTimesheet: Button = findViewById(R.id.button_timesheet)
        val buttonViewRecords: Button = findViewById(R.id.button_view_record)
        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val settingsFragment = SettingsFragment()

        makeCurrentFragment(homeFragment)

        NavBar.setOnNavigationItemSelectListener{
            when(it.itemId){
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_profile -> makeCurrentFragment(profileFragment)
                R.id.ic_settings -> makeCurrentFragment(settingsFragment)
            }
            true
        }



        // Get the current user's ID
        val userId = auth.currentUser?.uid

        // Display current date
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        textViewDate.text = currentDate

        // Update time every second
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
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
                    findViewById<TextView>(R.id.text_view_welcome_message).text = welcomeMessage
                }
            }
        }

        // Button click listeners
        buttonCategory.setOnClickListener {
            // Open Category Activity
            startActivity(Intent(this@DashboardActivity, CategoryActivity::class.java))
        }

        buttonTimesheet.setOnClickListener {
            // Open Timesheet Activity
            startActivity(Intent(this@DashboardActivity, TimesheetActivity::class.java))
        }

        buttonViewRecords.setOnClickListener {
            // Open ViewEntries Activity
            startActivity(Intent(this@DashboardActivity, ViewEntriesActivity::class.java))
        }
    }

    private fun makeCurrentFragment(fragment: androidx.fragment.app.Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}
