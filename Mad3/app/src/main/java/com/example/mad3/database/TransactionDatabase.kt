package com.example.mad3.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mad3.Transaction
import com.example.mad3.TransactionType
import java.util.Date

class TransactionDatabase(context: Context) : 
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_TRANSACTIONS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                label TEXT NOT NULL,
                amount REAL NOT NULL,
                type TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT,
                date INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    fun addTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val transaction = Transaction(
                    id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
                    label = getString(getColumnIndexOrThrow(COLUMN_LABEL)),
                    amount = getDouble(getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    type = TransactionType.valueOf(getString(getColumnIndexOrThrow(COLUMN_TYPE))),
                    category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    date = Date(getLong(getColumnIndexOrThrow(COLUMN_DATE)))
                )
                transactions.add(transaction)
            }
        }
        cursor.close()
        return transactions
    }

    fun updateTransaction(transaction: Transaction): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.name)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        return db.update(
            TABLE_TRANSACTIONS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(transaction.id.toString())
        )
    }

    fun deleteTransaction(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_TRANSACTIONS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun clearAllTransactions(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TRANSACTIONS, null, null)
    }

    companion object {
        private const val DATABASE_NAME = "transactions.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LABEL = "label"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
    }
} 