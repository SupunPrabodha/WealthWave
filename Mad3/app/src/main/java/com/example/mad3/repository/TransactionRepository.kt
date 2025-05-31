package com.example.mad3.repository

import androidx.lifecycle.LiveData
import com.example.mad3.db.TransactionDao
import com.example.mad3.model.Transaction
import com.example.mad3.model.TransactionType

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    fun getTotalAmountByType(type: TransactionType): LiveData<Double?> {
        return transactionDao.getTotalAmountByType(type)
    }

    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAllTransactions()
    }
} 