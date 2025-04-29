package com.subhashana.spendwise.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PasscodeManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "SpendWisePrefs"
        private const val KEY_PASSCODE = "passcode"
        private const val DEFAULT_PASSCODE = "1234"
    }

    fun setPasscode(passcode: String): Boolean {
        return try {
            if (passcode.length == 4 && passcode.all { it.isDigit() }) {
                sharedPreferences.edit().putString(KEY_PASSCODE, passcode).apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("PasscodeManager", "Error setting passcode", e)
            false
        }
    }

    fun getPasscode(): String {
        return sharedPreferences.getString(KEY_PASSCODE, DEFAULT_PASSCODE) ?: DEFAULT_PASSCODE
    }

    fun validatePasscode(input: String): Boolean {
        return try {
            input == getPasscode()
        } catch (e: Exception) {
            Log.e("PasscodeManager", "Error validating passcode", e)
            false
        }
    }

    fun isPasscodeSet(): Boolean {
        return sharedPreferences.contains(KEY_PASSCODE)
    }
} 