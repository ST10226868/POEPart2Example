package com.example.poepart2example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.poepart2example.fragments.DashboardFragment
import com.example.poepart2example.fragments.GraphFragment
import com.example.poepart2example.fragments.ProfileFragment
import com.example.poepart2example.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private val profileFragment = ProfileFragment()
    private val dashboardFragment = DashboardFragment()
    private val settingsFragment = SettingsFragment()
    private val graphFragment = GraphFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        replaceFragment(dashboardFragment)

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> replaceFragment(profileFragment)
                R.id.dashboard -> replaceFragment(dashboardFragment)
                R.id.settings -> replaceFragment(settingsFragment)
                R.id.graph -> replaceFragment(graphFragment)


            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_container, fragment)
            .commit()
    }
}
