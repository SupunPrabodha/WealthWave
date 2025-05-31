package com.example.mad3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Get references to views
        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        val appNameTextView = findViewById<TextView>(R.id.appNameTextView)
        val taglineTextView = findViewById<TextView>(R.id.taglineTextView)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        // Apply animations
        logoImageView.startAnimation(fadeIn)
        appNameTextView.startAnimation(slideUp)
        taglineTextView.startAnimation(slideUp)

        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user has completed onboarding
            val sharedPreferences = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE)
            val hasCompletedOnboarding = sharedPreferences.getBoolean("hasCompletedOnboarding", false)

            val intent = if (hasCompletedOnboarding) {
                // Check if user is logged in
                val loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
                
                if (isLoggedIn) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, LoginActivity::class.java)
                }
            } else {
                Intent(this, OnboardingActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 2000) // 2 seconds delay
    }
} 