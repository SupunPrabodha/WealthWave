package com.example.mad3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad3.databinding.ActivityMainBinding
import com.example.mad3.utils.BudgetManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionDatabase: TransactionDatabase
    private lateinit var userPreferences: UserPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Show a message that notifications are important for budget tracking
            Snackbar.make(
                binding.root,
                "Notifications are important for budget tracking",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

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

        transactionDatabase = TransactionDatabase(this)
        userPreferences = UserPreferences(this)

        // Request notification permission for Android 13+
        askNotificationPermission()

        setupNavigation()
        setupRecyclerView()
        setupFloatingActionButton()
        loadTransactions()
        updateBalance()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_add -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.navigation_analysis -> {
                    startActivity(Intent(this, AnalysisActivity::class.java))
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter({ transaction ->
            showTransactionDialog(transaction)
        }, userPreferences)
        
        binding.recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFloatingActionButton() {
        binding.addBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
    }

    private fun loadTransactions() {
        val transactions = transactionDatabase.getAllTransactions()
        transactionAdapter.submitList(transactions)
        updateDashboard(transactions)
        
        // Check budget status after loading transactions
        BudgetManager.checkBudgetStatus(this)
    }

    private fun updateDashboard(transactions: List<Transaction>) {
        val totalAmount = transactions.sumOf { it.amount }
        val incomeAmount = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenseAmount = kotlin.math.abs(transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount })
        val monthlyBudget = BudgetManager.getMonthlyBudget(this)

        binding.balance.text = "Rs. %.2f".format(totalAmount)
        binding.income.text = "Rs. %.2f".format(incomeAmount)
        binding.expense.text = "-Rs. %.2f".format(expenseAmount)
        binding.budget.text = "Rs. %.2f".format(monthlyBudget)

        // Update budget progress
        val budgetProgress = if (monthlyBudget > 0) {
            (expenseAmount / monthlyBudget * 100).toInt()
        } else {
            0
        }
        binding.budgetProgress.progress = budgetProgress

        // Show warning if expenses exceed 80% of budget
        if (monthlyBudget > 0 && expenseAmount > monthlyBudget * 0.8) {
            binding.budgetWarning.visibility = View.VISIBLE
        } else {
            binding.budgetWarning.visibility = View.GONE
        }
    }

    private fun showTransactionDialog(transaction: Transaction) {
        MaterialAlertDialogBuilder(this)
            .setTitle(transaction.label)
            .setMessage("""
                Amount: Rs. %.2f
                Type: ${transaction.type}
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
            putExtra("type", transaction.type.name)
            putExtra("category", transaction.category)
            putExtra("description", transaction.description)
        }
        updateTransactionLauncher.launch(intent)
    }

    private fun deleteTransaction(transaction: Transaction) {
        if (transactionDatabase.deleteTransaction(transaction.id) > 0) {
            loadTransactions()
            Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, "Failed to delete transaction", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateBalance() {
        val transactions = transactionDatabase.getAllTransactions()
        val balance = transactions.sumOf { it.amount }
        val formattedBalance = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(balance)
        binding.balance.text = formattedBalance
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}