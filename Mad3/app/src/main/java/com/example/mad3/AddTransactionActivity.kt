package com.example.mad3

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mad3.database.TransactionDatabase
import com.example.mad3.databinding.ActivityAddTransactionBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Date

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: TransactionDatabase
    private var currentType = TransactionType.EXPENSE
    private var expenseCategories = TransactionCategory.values()
        .filter { it.ordinal >= TransactionCategory.FOOD.ordinal }
        .map { it.label }
    private var incomeCategories = TransactionCategory.values()
        .filter { it.ordinal < TransactionCategory.FOOD.ordinal }
        .map { it.label }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = TransactionDatabase(this)

        setupNavigation()
        setupTypeToggle()
        setupCategoryDropdown()
        setupClickListeners()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_add
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    setResult(RESULT_CANCELED)
                    finish()
                    true
                }
                R.id.navigation_analysis -> {
                    setResult(RESULT_CANCELED)
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_settings -> {
                    setResult(RESULT_CANCELED)
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_add -> true
                else -> false
            }
        }

        binding.closeBtn.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun setupTypeToggle() {
        updateTypeButtons()

        binding.expenseButton.setOnClickListener {
            if (currentType != TransactionType.EXPENSE) {
                currentType = TransactionType.EXPENSE
                updateTypeButtons()
                setupCategoryDropdown()
            }
        }

        binding.incomeButton.setOnClickListener {
            if (currentType != TransactionType.INCOME) {
                currentType = TransactionType.INCOME
                updateTypeButtons()
                setupCategoryDropdown()
            }
        }
    }

    private fun updateTypeButtons() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary_color)
        val grayColor = ContextCompat.getColor(this, R.color.gray)

        binding.expenseButton.setTextColor(
            if (currentType == TransactionType.EXPENSE) primaryColor else grayColor
        )
        binding.incomeButton.setTextColor(
            if (currentType == TransactionType.INCOME) primaryColor else grayColor
        )
    }

    private fun setupCategoryDropdown() {
        val categories = if (currentType == TransactionType.EXPENSE) expenseCategories else incomeCategories
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        (binding.categoryInput as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.addTransactionBtn.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val label = binding.labelInput.text.toString()
        if (label.isEmpty()) {
            binding.labelLayout.error = "Please enter a label"
            isValid = false
        } else {
            binding.labelLayout.error = null
        }

        val category = binding.categoryInput.text.toString()
        if (category.isEmpty()) {
            binding.categoryLayout.error = "Please select a category"
            isValid = false
        } else {
            binding.categoryLayout.error = null
        }

        val amountStr = binding.amountInput.text.toString()
        if (amountStr.isEmpty()) {
            binding.amountLayout.error = "Please enter an amount"
            isValid = false
        } else {
            try {
                amountStr.toDouble()
                binding.amountLayout.error = null
            } catch (e: NumberFormatException) {
                binding.amountLayout.error = "Please enter a valid amount"
                isValid = false
            }
        }

        return isValid
    }

    private fun saveTransaction() {
        val label = binding.labelInput.text.toString()
        val amount = binding.amountInput.text.toString().toDouble()
        val category = binding.categoryInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        
        // Convert amount to positive/negative based on type
        val finalAmount = if (currentType == TransactionType.EXPENSE) -amount else amount

        val transaction = Transaction(
            label = label,
            amount = finalAmount,
            type = currentType,
            category = category,
            description = description,
            date = Date()
        )

        val id = db.addTransaction(transaction)
        if (id > 0) {
            setResult(RESULT_OK)
            finish()
        } else {
            Snackbar.make(binding.root, "Failed to add transaction", Snackbar.LENGTH_SHORT).show()
        }
    }
}