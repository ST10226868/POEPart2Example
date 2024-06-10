package com.example.poepart2example.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.poepart2example.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class GraphFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graph, container, false)
        lineChart = view.findViewById(R.id.line_chart)
        fetchGraphData()
        return view
    }

    private fun fetchGraphData() {
        db.collection("timesheet_entries")
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val entries = ArrayList<Entry>()
                var index = 0f
                for (document in result) {
                    val startDate = document.getString("startDate") ?: ""
                    val hoursWorked = calculateHoursWorked(
                        document.getString("startTime") ?: "",
                        document.getString("endTime") ?: ""
                    )
                    entries.add(Entry(index, hoursWorked))
                    index++
                }
                val dataSet = LineDataSet(entries, "Hours Worked")
                dataSet.color = Color.BLUE
                dataSet.valueTextColor = Color.BLACK

                val lineData = LineData(dataSet)
                lineChart.data = lineData

                val legend = lineChart.legend
                legend.isEnabled = true
                legend.form = Legend.LegendForm.LINE
                legend.textColor = Color.BLACK

                lineChart.invalidate() // refresh
            }
    }

    private fun calculateHoursWorked(startTime: String, endTime: String): Float {
        // Define date format
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        try {
            // Parse startTime and endTime strings into Date objects
            val startDate = dateFormat.parse(startTime)
            val endDate = dateFormat.parse(endTime)

            // Calculate the difference in milliseconds
            val differenceMillis = endDate.time - startDate.time

            // Convert milliseconds to hours (1 hour = 3600000 milliseconds)
            val differenceHours = differenceMillis.toFloat() / 3600000

            return differenceHours
        } catch (e: Exception) {
            // Handle parsing errors
            e.printStackTrace()
            return -1f // Return -1 if an error occurs
        }
    }


}
