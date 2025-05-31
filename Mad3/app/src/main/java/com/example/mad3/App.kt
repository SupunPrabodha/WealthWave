package com.example.mad3

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Load and apply saved theme preference
        val isDarkTheme = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("dark_theme", false)
        
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
} 