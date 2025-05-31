package com.example.mad3.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (BudgetManager.shouldShowDailyReminder(applicationContext)) {
                NotificationHelper.showDailyReminderNotification(applicationContext)
                BudgetManager.updateLastNotificationDate(applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "daily_expense_reminder"
    }
} 