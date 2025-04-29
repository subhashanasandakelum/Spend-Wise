package com.subhashana.spendwise.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "SpendWisePrefs"
        private const val KEY_THEME_MODE = "theme_mode"
        const val MODE_SYSTEM = 0
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2
    }

    fun setThemeMode(mode: Int) {
        sharedPreferences.edit().putInt(KEY_THEME_MODE, mode).apply()
        applyTheme(mode)
    }

    fun getThemeMode(): Int {
        return sharedPreferences.getInt(KEY_THEME_MODE, MODE_SYSTEM)
    }

    fun applyTheme(mode: Int = getThemeMode()) {
        when (mode) {
            MODE_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            MODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun isNightMode(): Boolean {
        return when (getThemeMode()) {
            MODE_LIGHT -> false
            MODE_DARK -> true
            else -> {
                val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                currentNightMode == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
} 