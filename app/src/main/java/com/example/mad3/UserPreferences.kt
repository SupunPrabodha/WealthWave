package com.example.mad3

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "user_preferences",
        Context.MODE_PRIVATE
    )

    var monthlyBudget: Double
        get() = sharedPreferences.getFloat("monthly_budget", 0f).toDouble()
        set(value) = sharedPreferences.edit().putFloat("monthly_budget", value.toFloat()).apply()

    var currency: String
        get() = sharedPreferences.getString("currency", "$") ?: "$"
        set(value) = sharedPreferences.edit().putString("currency", value).apply()
} 