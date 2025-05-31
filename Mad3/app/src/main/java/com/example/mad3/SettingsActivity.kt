package com.example.mad3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.mad3.databinding.ActivitySettingsBinding
import com.example.mad3.utils.BackupUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var budgetInput: TextInputEditText
    private lateinit var currencyInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var backupUtils: BackupUtils

    private val restoreFilePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleRestoreFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        budgetInput = binding.budgetInput
        currencyInput = binding.currencyInput
        saveButton = binding.saveButton

        backupUtils = BackupUtils(this)

        loadSavedSettings()
        setupSaveButton()
        setupNavigation()
        setupThemeSwitch()
        setupBackupButtons()
    }

    private fun loadSavedSettings() {
        val sharedPreferences = getSharedPreferences("WealthWavePrefs", MODE_PRIVATE)
        val savedBudget = sharedPreferences.getString("monthly_budget", "")
        val savedCurrency = sharedPreferences.getString("currency", "USD")

        budgetInput.setText(savedBudget)
        currencyInput.setText(savedCurrency)
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val budget = budgetInput.text.toString()
            val currency = currencyInput.text.toString()

            if (budget.isNotEmpty() && currency.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences("WealthWavePrefs", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("monthly_budget", budget)
                    putString("currency", currency)
                    apply()
                }
                Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupThemeSwitch() {
        // Get current theme state
        val isDarkTheme = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("dark_theme", false)
        
        // Set initial switch state
        binding.themeSwitch.isChecked = isDarkTheme

        // Set up switch listener
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save theme preference
            getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putBoolean("dark_theme", isChecked)
                .apply()

            // Apply theme
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_settings
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_settings -> true
                else -> false
            }
        }
    }

    private fun setupBackupButtons() {
        binding.backupButton.setOnClickListener {
            performBackup()
        }

        binding.restoreButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Restore Data")
                .setMessage("This will replace all existing transactions with the backup data. Are you sure you want to continue?")
                .setPositiveButton("Restore") { _, _ ->
                    restoreFilePicker.launch("application/json")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun performBackup() {
        val file = backupUtils.saveBackupToFile()
        if (file != null) {
            shareBackupFile(file)
        } else {
            Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareBackupFile(file: File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share Backup File"))
    }

    private fun handleRestoreFile(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val tempFile = File(cacheDir, "temp_backup.json")
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                if (backupUtils.restoreFromFile(tempFile)) {
                    Toast.makeText(this, "Data restored successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to restore data", Toast.LENGTH_SHORT).show()
                }

                tempFile.delete()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading backup file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
} 