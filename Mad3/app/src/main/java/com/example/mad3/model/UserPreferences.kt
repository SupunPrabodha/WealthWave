package com.example.mad3.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var monthlyBudget: Double
        get() = prefs.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
        set(value) = prefs.edit { putFloat(KEY_MONTHLY_BUDGET, value.toFloat()) }

    var currency: String
        get() = prefs.getString(KEY_CURRENCY, "USD") ?: "USD"
        set(value) = prefs.edit { putString(KEY_CURRENCY, value) }

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var lastBackupDate: Long
        get() = prefs.getLong(KEY_LAST_BACKUP, 0)
        set(value) = prefs.edit { putLong(KEY_LAST_BACKUP, value) }

    companion object {
        private const val PREFS_NAME = "finance_tracker_prefs"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LAST_BACKUP = "last_backup"
    }
} 