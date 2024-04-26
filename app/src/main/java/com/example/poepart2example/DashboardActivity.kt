package com.example.poepart2example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        val textViewDate: TextView = findViewById(R.id.text_view_date)
        val textViewTime: TextView = findViewById(R.id.text_view_time)
        val buttonCategory: Button = findViewById(R.id.button_category)
        val buttonTimesheet: Button = findViewById(R.id.button_timesheet)

        // Display current date
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        textViewDate.text = currentDate

        // Update time every second
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                    textViewTime.text = currentTime
                }
            }
        }, 0, 1000)

        // Button click listeners
        buttonCategory.setOnClickListener {
            // Open Category Activity
            startActivity(Intent(this@DashboardActivity, CategoryActivity::class.java))
        }

        buttonTimesheet.setOnClickListener {
            // Open Timesheet Activity
            startActivity(Intent(this@DashboardActivity, TimesheetActivity::class.java))
        }
    }
}
