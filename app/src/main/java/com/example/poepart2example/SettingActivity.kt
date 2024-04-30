package com.example.poepart2example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatDelegate

class SettingActivity : AppCompatActivity()
{

    private lateinit var toggler: CheckBox

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        toggler = findViewById(R.id.toggler)

        // Set initial theme based on user preference
        if (isDarkModeEnabled())
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toggler.isChecked = true
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toggler.isChecked = false
        }

        // Toggle between light and dark mode
        toggler.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkModeState(true)
            } else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModeState(false)
            }
            recreate()
        }
    }

    // Save dark mode state
    private fun saveDarkModeState(isDarkModeEnabled: Boolean)
    {
        val sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("darkMode", isDarkModeEnabled)
        editor.apply()
    }

    // Check if dark mode is enabled
    private fun isDarkModeEnabled(): Boolean
    {
        val sharedPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        return sharedPrefs.getBoolean("darkMode", false)
    }
}
