package com.example.mad3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad3.database.TransactionDatabase
import com.example.mad3.databinding.ActivityMainBinding
import com.example.mad3.model.UserPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var db: TransactionDatabase
    private lateinit var userPreferences: UserPreferences

    private val addTransactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadTransactions()
        }
    }

    private val updateTransactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadTransactions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TransactionDatabase(this)
        userPreferences = UserPreferences(this)

        setupNavigation()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_add -> {
                    addTransactionLauncher.launch(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.navigation_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            showTransactionDialog(transaction)
        }
        
        binding.recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupClickListeners() {
        binding.addBtn.setOnClickListener {
            addTransactionLauncher.launch(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun loadData() {
        loadTransactions()
    }

    private fun loadTransactions() {
        val transactions = db.getAllTransactions()
        transactionAdapter.updateTransactions(transactions)
        updateDashboard(transactions)
    }

    private fun updateDashboard(transactions: List<Transaction>) {
        val totalAmount = transactions.sumOf { it.amount }
        val incomeAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val expenseAmount = kotlin.math.abs(transactions.filter { it.amount < 0 }.sumOf { it.amount })

        binding.balance.text = "$%.2f".format(totalAmount)
        binding.budget.text = "$%.2f".format(incomeAmount)
        binding.expense.text = "-$%.2f".format(expenseAmount)
    }

    private fun showTransactionDialog(transaction: Transaction) {
        MaterialAlertDialogBuilder(this)
            .setTitle(transaction.label)
            .setMessage("""
                Amount: $%.2f
                Category: ${transaction.category}
                ${if (transaction.description.isNotEmpty()) "\nDescription: ${transaction.description}" else ""}
            """.trimIndent().format(transaction.amount))
            .setPositiveButton("Delete") { _, _ ->
                deleteTransaction(transaction)
            }
            .setNegativeButton("Edit") { _, _ ->
                editTransaction(transaction)
            }
            .setNeutralButton("Close", null)
            .show()
    }

    private fun editTransaction(transaction: Transaction) {
        val intent = Intent(this, UpdateTransactionActivity::class.java).apply {
            putExtra("transaction_id", transaction.id)
            putExtra("label", transaction.label)
            putExtra("amount", transaction.amount)
            putExtra("type", transaction.type)
            putExtra("category", transaction.category)
            putExtra("description", transaction.description)
        }
        updateTransactionLauncher.launch(intent)
    }

    private fun deleteTransaction(transaction: Transaction) {
        if (db.deleteTransaction(transaction.id) > 0) {
            loadTransactions()
            Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, "Failed to delete transaction", Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 100
    }
}