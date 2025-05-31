package com.example.mad3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.mad3.databinding.LayoutBaseBinding

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var baseBinding: LayoutBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = LayoutBaseBinding.inflate(layoutInflater)
        setContentView(baseBinding.root)

        // Setup bottom navigation
        baseBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (!isCurrentActivity(MainActivity::class.java)) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_analysis -> {
                    if (!isCurrentActivity(AnalysisActivity::class.java)) {
                        startActivity(Intent(this, AnalysisActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.navigation_settings -> {
                    if (!isCurrentActivity(SettingsActivity::class.java)) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        // Set the selected navigation item based on the current activity
        baseBinding.bottomNavigation.selectedItemId = when {
            isCurrentActivity(MainActivity::class.java) -> R.id.navigation_home
            isCurrentActivity(AnalysisActivity::class.java) -> R.id.navigation_analysis
            isCurrentActivity(SettingsActivity::class.java) -> R.id.navigation_settings
            else -> R.id.navigation_home
        }

        // Set the toolbar title
        setSupportActionBar(baseBinding.header.toolbar)
    }

    private fun <T> isCurrentActivity(activityClass: Class<T>): Boolean {
        return this::class.java == activityClass
    }

    protected fun <B : ViewBinding> setActivityContent(
        inflate: (LayoutInflater, ViewGroup, Boolean) -> B,
        bind: (B) -> Unit
    ) {
        val contentBinding = inflate(layoutInflater, baseBinding.contentContainer, true)
        bind(contentBinding)
    }
} 