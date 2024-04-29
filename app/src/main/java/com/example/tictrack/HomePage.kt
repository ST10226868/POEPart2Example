package com.example.tictrack


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class HomePage : AppCompatActivity() {
    private lateinit var Date: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        Date = findViewById(R.id.txtDate)

        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        Date.text = currentDate

        val btnCreateCategory: Button = findViewById(R.id.btnCreateCategory)
        btnCreateCategory.setOnClickListener {
            startActivity(Intent(this@HomePage, CreateCategory::class.java))
        }

        val btnCreateTimeSheet: Button = findViewById(R.id.btnCreateTimeSheet)
        btnCreateTimeSheet.setOnClickListener {
            startActivity(Intent(this@HomePage, CreateTimeSheet::class.java))
        }

            val btnReport: Button = findViewById(R.id.btnReport)
            btnReport.setOnClickListener {
                startActivity(Intent(this@HomePage, Report::class.java))


            }
        }
    }


