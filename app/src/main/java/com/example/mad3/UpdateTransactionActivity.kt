package com.example.mad3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mad3.database.TransactionDatabase
import com.example.mad3.databinding.ActivityAddTransactionBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Date

class UpdateTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: TransactionDatabase
    private var currentType = TransactionType.EXPENSE
    private var transactionId: Long = 0
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

        // Get transaction details from intent
        transactionId = intent.getLongExtra("transaction_id", 0)
        val label = intent.getStringExtra("label") ?: ""
        val amount = intent.getDoubleExtra("amount", 0.0)
        val type = intent.getSerializableExtra("type") as? TransactionType ?: TransactionType.EXPENSE
        val category = intent.getStringExtra("category") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        currentType = type
        setupTypeToggle()
        setupCategoryDropdown()
        setupClickListeners()
        
        // Fill in the existing data
        binding.labelInput.setText(label)
        binding.amountInput.setText(String.format("%.2f", kotlin.math.abs(amount)))
        binding.categoryInput.setText(category)
        binding.descriptionInput.setText(description)
        
        // Update UI elements for update mode
        binding.addTransactionBtn.text = "Update Transaction"
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
                updateTransaction()
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

    private fun updateTransaction() {
        val label = binding.labelInput.text.toString()
        val amount = binding.amountInput.text.toString().toDouble()
        val category = binding.categoryInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        
        // Convert amount to positive/negative based on type
        val finalAmount = if (currentType == TransactionType.EXPENSE) -amount else amount

        val transaction = Transaction(
            id = transactionId,
            label = label,
            amount = finalAmount,
            type = currentType,
            category = category,
            description = description,
            date = Date()
        )

        val rowsAffected = db.updateTransaction(transaction)
        if (rowsAffected > 0) {
            setResult(RESULT_OK)
            finish()
        } else {
            Snackbar.make(binding.root, "Failed to update transaction", Snackbar.LENGTH_SHORT).show()
        }
    }
} 