package com.example.mad3.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mad3.MainActivity
import com.example.mad3.R
import java.text.NumberFormat
import java.util.Locale

object NotificationHelper {
    private const val TAG = "NotificationHelper"
    private const val CHANNEL_ID = "budget_notifications"
    private const val CHANNEL_NAME = "Budget Notifications"
    private const val CHANNEL_DESCRIPTION = "Notifications for budget updates and alerts"

    // Notification IDs
    const val NOTIFICATION_BUDGET_ADDED = 1
    const val NOTIFICATION_BUDGET_UPDATED = 2
    const val NOTIFICATION_BUDGET_DELETED = 3
    const val NOTIFICATION_BUDGET_EXCEEDED = 4
    const val NOTIFICATION_BUDGET_WARNING = 5
    const val NOTIFICATION_DAILY_REMINDER = 6

    private var notificationId = 0

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildBasicNotification(
        context: Context,
        title: String,
        content: String
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    fun showBudgetAddedNotification(context: Context, budgetType: String) {
        val notification = buildBasicNotification(
            context,
            "Budget Set",
            "$budgetType budget has been set successfully"
        ).build()

        showNotification(context, notification)
    }

    fun showBudgetUpdatedNotification(context: Context, budgetType: String) {
        val notification = buildBasicNotification(
            context,
            "Budget Updated",
            "$budgetType budget has been updated"
        ).build()

        showNotification(context, notification)
    }

    fun showBudgetDeletedNotification(context: Context, budgetName: String) {
        Log.d(TAG, "Showing budget deleted notification for: $budgetName")
        val notification = buildBasicNotification(
            context,
            "Transaction Deleted",
            "'$budgetName' has been deleted"
        ).build()

        showNotification(context, notification)
    }

    fun showBudgetExceededNotification(context: Context, budgetType: String, exceededAmount: Double) {
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale.getDefault())
            .format(exceededAmount)
            
        val notification = buildBasicNotification(
            context,
            "Budget Exceeded",
            "You have exceeded your $budgetType budget by $formattedAmount"
        ).build()

        showNotification(context, notification)
    }

    fun showBudgetWarningNotification(context: Context, budgetType: String, remainingPercentage: Int) {
        val notification = buildBasicNotification(
            context,
            "Budget Warning",
            "You have only $remainingPercentage% of your $budgetType budget remaining"
        ).build()

        showNotification(context, notification)
    }

    fun showDailyReminderNotification(context: Context) {
        Log.d(TAG, "Showing daily reminder notification")
        val notification = buildBasicNotification(
            context,
            "Daily Expense Reminder",
            "Don't forget to record your expenses for today!"
        ).setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        showNotification(context, notification)
    }






























    private fun showNotification(context: Context, notification: android.app.Notification) {
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId++, notification)
            }
        } catch (e: SecurityException) {
            // Handle the case where notification permission is not granted
            e.printStackTrace()
        }
    }
} 