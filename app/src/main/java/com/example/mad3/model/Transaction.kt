package com.example.mad3.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val date: Date,
    val note: String? = null
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory {
    FOOD,
    TRANSPORT,
    BILLS,
    ENTERTAINMENT,
    SHOPPING,
    HEALTH,
    EDUCATION,
    SALARY,
    INVESTMENT,
    OTHER
} 