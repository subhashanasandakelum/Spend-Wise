package com.subhashana.spendwise.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.Currency
import java.util.Locale
import java.text.NumberFormat

class CurrencyManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "SpendWisePrefs"
        private const val KEY_CURRENCY = "currency"
        private const val DEFAULT_CURRENCY = "LKR"
        
        val SUPPORTED_CURRENCIES = listOf(
            "LKR", // Sri Lankan Rupee
            "USD", // US Dollar
            "EUR", // Euro
            "GBP", // British Pound
            "JPY", // Japanese Yen
            "AUD", // Australian Dollar
            "CAD", // Canadian Dollar
            "INR", // Indian Rupee
            "SGD", // Singapore Dollar
            "AED"  // UAE Dirham
        )
    }

    fun setCurrency(currencyCode: String): Boolean {
        return try {
            if (currencyCode in SUPPORTED_CURRENCIES) {
                sharedPreferences.edit().putString(KEY_CURRENCY, currencyCode).apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("CurrencyManager", "Error setting currency", e)
            false
        }
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun getCurrencySymbol(): String {
        return try {
            Currency.getInstance(getCurrency()).symbol
        } catch (e: Exception) {
            Log.e("CurrencyManager", "Error getting currency symbol", e)
            "Rs."
        }
    }

    fun getCurrencyLocale(): Locale {
        return when (getCurrency()) {
            "LKR" -> Locale("si", "LK")
            "USD" -> Locale.US
            "EUR" -> Locale.GERMANY
            "GBP" -> Locale.UK
            "JPY" -> Locale.JAPAN
            "AUD" -> Locale("en", "AU")
            "CAD" -> Locale.CANADA
            "INR" -> Locale("en", "IN")
            "SGD" -> Locale("en", "SG")
            "AED" -> Locale("ar", "AE")
            else -> Locale.getDefault()
        }
    }

    fun formatAmount(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(getCurrencyLocale())
        format.currency = Currency.getInstance(getCurrency())
        return format.format(amount)
    }
} 