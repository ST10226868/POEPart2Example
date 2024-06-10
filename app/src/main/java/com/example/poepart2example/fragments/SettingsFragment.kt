package com.example.poepart2example.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.poepart2example.R

class SettingsFragment : Fragment() {

    private lateinit var toggler: CheckBox
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        toggler = view.findViewById(R.id.toggler)
        sharedPrefs = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

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
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModeState(false)
            }
            requireActivity().recreate()
        }

        return view
    }

    // Save dark mode state
    private fun saveDarkModeState(isDarkModeEnabled: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("darkMode", isDarkModeEnabled)
        editor.apply()
    }

    // Check if dark mode is enabled
    private fun isDarkModeEnabled(): Boolean {
        return sharedPrefs.getBoolean("darkMode", false)
    }
}
