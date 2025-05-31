package com.example.mad3.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mad3.Transaction
import com.example.mad3.TransactionType
import java.util.Date

class TransactionDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.toString())
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val label = getString(getColumnIndexOrThrow(COLUMN_LABEL))
                val amount = getDouble(getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = TransactionType.valueOf(getString(getColumnIndexOrThrow(COLUMN_TYPE)))
                val category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY))
                val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val date = Date(getLong(getColumnIndexOrThrow(COLUMN_DATE)))

                transactions.add(Transaction(id, label, amount, type, category, description, date))
            }
        }
        cursor.close()
        return transactions
    }

    fun deleteTransaction(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateTransaction(transaction: Transaction): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LABEL, transaction.label)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type.toString())
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DESCRIPTION, transaction.description)
            put(COLUMN_DATE, transaction.date.time)
        }
        return db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(transaction.id.toString())
        )
    }

    fun clearAllTransactions(): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }
} 