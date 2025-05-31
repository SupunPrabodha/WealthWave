package com.example.mad3

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val label: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val description: String = "",
    val date: Date = Date()
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory(val label: String) {
    // Income categories
    SALARY("Salary"),
    BUSINESS("Business"),
    INVESTMENT("Investment"),
    GIFT("Gift"),
    
    // Expense categories
    FOOD("Food & Dining"),
    TRANSPORTATION("Transportation"),
    UTILITIES("Utilities"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
    EDUCATION("Education"),
    OTHER("Other")
}