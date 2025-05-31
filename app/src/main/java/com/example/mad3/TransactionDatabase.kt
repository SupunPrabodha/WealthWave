package com.example.mad3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mad3.utils.BudgetManager
import com.example.mad3.utils.NotificationHelper
import java.util.Date

class TransactionDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val appContext = context.applicationContext

    companion object {
        private const val DATABASE_NAME = "transactions.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LABEL = "label"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LABEL TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTransaction(transaction: Transaction): Long {
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        val id = writableDatabase.insert(TABLE_NAME, null, values)
        
        if (id != -1L) {
            // Show notification for successful transaction addition
            NotificationHelper.showBudgetAddedNotification(appContext, transaction.description)
            
            // Check budget status after adding a transaction
            if (transaction.type == TransactionType.EXPENSE) {
                BudgetManager.checkBudgetStatus(appContext)
            }
        }
        
        return id
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val label = it.getString(it.getColumnIndexOrThrow(COLUMN_LABEL))
                val amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = TransactionType.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)))
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val date = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE)))

                transactions.add(Transaction(id, label, amount, type, category, description, date))
            }
        }
        return transactions
    }

    fun deleteTransaction(id: Long): Int {
        val transaction = getTransactionById(id) ?: return 0
        val result = writableDatabase.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        
        if (result > 0) {
            // Show delete notification
            NotificationHelper.showBudgetDeletedNotification(appContext, transaction.description)
            
            // Check budget status after deleting a transaction
            if (transaction.type == TransactionType.EXPENSE) {
                BudgetManager.checkBudgetStatus(appContext)
            }
        }
        
        return result
    }

    private fun getTransactionById(id: Long): Transaction? {
        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                val label = it.getString(it.getColumnIndexOrThrow(COLUMN_LABEL))
                val amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = TransactionType.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)))
                val category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val date = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE)))

                Transaction(id, label, amount, type, category, description, date)
            } else null
        }
    }

    fun updateTransaction(transaction: Transaction): Int {
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        
        val result = writableDatabase.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(transaction.id.toString())
        )
        
        if (result > 0) {
            // Show update notification
            NotificationHelper.showBudgetUpdatedNotification(appContext, transaction.description)
            
            // Check budget status after updating a transaction
            if (transaction.type == TransactionType.EXPENSE) {
                BudgetManager.checkBudgetStatus(appContext)
            }
        }
        
        return result
    }

    fun clearAllTransactions(): Int {
        return writableDatabase.delete(TABLE_NAME, null, null)
    }
}