package com.example.mad3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var signing: android.widget.TextView
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var emailErrorTextView: android.widget.TextView
    private lateinit var passwordErrorTextView: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        emailErrorTextView = findViewById(R.id.emailErrorTextView)
        passwordErrorTextView = findViewById(R.id.passwordErrorTextView)

        // Set up login button click listener
        findViewById<com.google.android.material.button.MaterialButton>(R.id.loginButton).setOnClickListener {
            validateAndLogin()
        }

        // Add text change listeners to clear errors when user types
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailErrorTextView.visibility = View.GONE
            }
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordErrorTextView.visibility = View.GONE
            }
        }
    }

    private fun validateAndLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Reset error messages
        emailErrorTextView.visibility = View.GONE
        passwordErrorTextView.visibility = View.GONE

        var isValid = true

        // Email validation
        if (email.isEmpty()) {
            emailErrorTextView.text = "Email is required"
            emailErrorTextView.visibility = View.VISIBLE
            isValid = false
        } else if (email != "supun@gmail.com") {
            emailErrorTextView.text = "Invalid email"
            emailErrorTextView.visibility = View.VISIBLE
            isValid = false
        }

        // Password validation
        if (password.isEmpty()) {
            passwordErrorTextView.text = "Password is required"
            passwordErrorTextView.visibility = View.VISIBLE
            isValid = false
        } else if (password != "Supun123") {
            passwordErrorTextView.text = "Invalid password"
            passwordErrorTextView.visibility = View.VISIBLE
            isValid = false
        }

        if (isValid) {
            // Save login state in SharedPreferences
            val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean("isLoggedIn", true)
                apply()
            }

            // Show success message
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Navigate to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        signing = findViewById(R.id.toggleTextView)
        signing.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}