package com.example.mad3.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object BudgetManager {
    private const val TAG = "BudgetManager"
    private const val PREFS_NAME = "BudgetPrefs"
    private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    private const val KEY_LAST_NOTIFICATION_DATE = "last_notification_date"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setMonthlyBudget(context: Context, amount: Double) {
        val previousBudget = getMonthlyBudget(context)
        getSharedPreferences(context).edit().apply {
            putFloat(KEY_MONTHLY_BUDGET, amount.toFloat())
            apply()
        }
        Log.d(TAG, "Monthly budget set: $amount (Previous: $previousBudget)")
        
        if (previousBudget == 0.0) {
            NotificationHelper.showBudgetAddedNotification(context, "Monthly")
        } else {
            NotificationHelper.showBudgetUpdatedNotification(context, "Monthly")
        }
        
        // Check budget status after setting/updating
        checkBudgetStatus(context)
    }

    fun getMonthlyBudget(context: Context): Double {
        return getSharedPreferences(context).getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    fun checkBudgetStatus(context: Context) {
        val monthlyBudget = getMonthlyBudget(context)
        if (monthlyBudget == 0.0) {
            Log.d(TAG, "No monthly budget set")
            return
        }

        val currentExpenses = calculateCurrentMonthExpenses(context)
        Log.d(TAG, "Current month expenses: $currentExpenses, Monthly budget: $monthlyBudget")

        val remainingBudget = monthlyBudget - currentExpenses
        val remainingPercentage = ((remainingBudget / monthlyBudget) * 100).toInt()

        when {
            remainingBudget < 0 -> {
                Log.d(TAG, "Budget exceeded by ${-remainingBudget}")
                NotificationHelper.showBudgetExceededNotification(context, "Monthly", -remainingBudget)
            }
            remainingPercentage <= 20 -> {
                Log.d(TAG, "Budget warning: $remainingPercentage% remaining")
                NotificationHelper.showBudgetWarningNotification(context, "Monthly", remainingPercentage)
            }
        }
    }

    private fun calculateCurrentMonthExpenses(context: Context): Double {
        // Implementation of expense calculation
        // This should be implemented based on your expense tracking system
        return 0.0 // Placeholder return
    }

    fun shouldShowDailyReminder(context: Context): Boolean {
        val lastNotificationDate = getLastNotificationDate(context)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val shouldShow = lastNotificationDate != today
        Log.d(TAG, "Should show daily reminder: $shouldShow (Last notification: $lastNotificationDate)")
        return shouldShow
    }

    private fun getLastNotificationDate(context: Context): String {
        return getSharedPreferences(context).getString(KEY_LAST_NOTIFICATION_DATE, "") ?: ""
    }

    fun updateLastNotificationDate(context: Context) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        getSharedPreferences(context).edit().apply {
            putString(KEY_LAST_NOTIFICATION_DATE, today)
            apply()
        }
        Log.d(TAG, "Updated last notification date to: $today")
    }
} 