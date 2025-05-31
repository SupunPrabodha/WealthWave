package com.example.mad3.utils

import android.content.Context
import com.example.mad3.Transaction
import com.example.mad3.database.TransactionDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class BackupUtils(private val context: Context) {
    private val gson = Gson()
    private val db = TransactionDatabase(context)
    
    fun exportData(): String {
        val transactions = db.getAllTransactions()
        val backupData = BackupData(
            transactions = transactions,
            timestamp = System.currentTimeMillis()
        )
        return gson.toJson(backupData)
    }

    fun importData(jsonData: String): Boolean {
        return try {
            val type = object : TypeToken<BackupData>() {}.type
            val backupData = gson.fromJson<BackupData>(jsonData, type)
            
            db.writableDatabase.beginTransaction()
            try {

                db.clearAllTransactions()
                backupData.transactions.forEach { transaction ->
                    db.addTransaction(transaction)
                }
                db.writableDatabase.setTransactionSuccessful()
                true
            } finally {
                db.writableDatabase.endTransaction()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun saveBackupToFile(): File? {
        val jsonData = exportData()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "wealthwave_backup_${dateFormat.format(Date())}.json"
        
        return try {
            val file = File(context.getExternalFilesDir(null), fileName)
            FileWriter(file).use { writer ->
                writer.write(jsonData)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun restoreFromFile(file: File): Boolean {
        return try {
            val jsonData = FileReader(file).use { reader ->
                reader.readText()
            }
            importData(jsonData)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    data class BackupData(
        val transactions: List<Transaction>,
        val timestamp: Long
    )
} 