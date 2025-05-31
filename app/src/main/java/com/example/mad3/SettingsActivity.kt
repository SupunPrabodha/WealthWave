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
import com.example.mad3.utils.BudgetManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var budgetInput: TextInputEditText
    private lateinit var currencyInput: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var backupUtils: BackupUtils
    private lateinit var userPreferences: UserPreferences

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
        userPreferences = UserPreferences(this)
        backupUtils = BackupUtils(this)

        loadSavedSettings()
        setupSaveButton()
        setupNavigation()
        setupThemeSwitch()
        setupSettings()
    }

    private fun loadSavedSettings() {
        val budget = BudgetManager.getMonthlyBudget(this)
        currencyInput.setText("Rs.")
        budgetInput.setText(if (budget > 0) budget.toString() else "")
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val budgetText = budgetInput.text.toString()
            val currencyText = "Rs."  // Always use Rs. as currency

            if (budgetText.isNotEmpty()) {
                try {
                    val budget = budgetText.toDouble()
                    if (budget < 0) {
                        Toast.makeText(this, "Budget cannot be negative", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    BudgetManager.setMonthlyBudget(this, budget)
                    userPreferences.currency = currencyText
                    
                    Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                    
                    startActivity(Intent(this, MainActivity::class.java))
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a monthly budget", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupThemeSwitch() {
        val isDarkTheme = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("dark_theme", false)
        
        binding.themeSwitch.isChecked = isDarkTheme

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences("settings", MODE_PRIVATE)
                .edit()
                .putBoolean("dark_theme", isChecked)
                .apply()

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
                    true
                }
                R.id.navigation_add -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.navigation_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    true
                }
                R.id.navigation_settings -> true
                else -> false
            }
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

    private fun setupSettings() {
        budgetInput.setOnClickListener {
            val currentBudget = BudgetManager.getMonthlyBudget(this)
            budgetInput.setText(if (currentBudget > 0) currentBudget.toString() else "")
        }

        binding.backupPreference.setOnClickListener {
            // Show backup/restore dialog
            MaterialAlertDialogBuilder(this)
                .setTitle("Backup & Restore")
                .setItems(arrayOf("Create Backup", "Restore from Backup")) { _, which ->
                    when (which) {
                        0 -> performBackup() // Create Backup
                        1 -> { // Restore from Backup
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
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
} 