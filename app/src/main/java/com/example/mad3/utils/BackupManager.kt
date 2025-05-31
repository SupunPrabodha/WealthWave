package com.example.mad3.utils

import android.content.Context
import android.util.Log
import com.example.mad3.Transaction
import com.example.mad3.TransactionDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {
    private const val TAG = "BackupManager"
    private const val BACKUP_FOLDER = "wealthwave_backups"
    private const val BACKUP_EXTENSION = ".json"

    fun exportData(context: Context, transactionDatabase: TransactionDatabase): Boolean {
        try {
            val transactions = transactionDatabase.getAllTransactions()
            val gson = Gson()
            val jsonString = gson.toJson(transactions)

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "wealthwave_backup_$timestamp$BACKUP_EXTENSION"

            val backupDir = File(context.getExternalFilesDir(null), BACKUP_FOLDER)
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            val backupFile = File(backupDir, fileName)
            FileOutputStream(backupFile).use { output ->
                output.write(jsonString.toByteArray())
            }

            Log.d(TAG, "Backup created successfully: ${backupFile.absolutePath}")
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Error creating backup", e)
            return false
        }
    }

    fun importData(context: Context, transactionDatabase: TransactionDatabase, backupFile: File): Boolean {
        try {
            FileInputStream(backupFile).use { input ->
                val jsonString = input.bufferedReader().use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<List<Transaction>>() {}.type
                val transactions = gson.fromJson<List<Transaction>>(jsonString, type)

                // Clear existing transactions
                transactionDatabase.clearAllTransactions()

                // Import new transactions
                transactions.forEach { transaction ->
                    transactionDatabase.addTransaction(transaction)
                }

                Log.d(TAG, "Backup restored successfully")
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring backup", e)
            return false
        }
    }

    fun getBackupFiles(context: Context): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), BACKUP_FOLDER)
        return if (backupDir.exists()) {
            backupDir.listFiles()?.filter { it.name.endsWith(BACKUP_EXTENSION) }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun deleteBackup(backupFile: File): Boolean {
        return try {
            backupFile.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting backup", e)
            false
        }
    }
} 