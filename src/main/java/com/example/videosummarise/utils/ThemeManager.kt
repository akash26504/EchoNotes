package com.example.videosummarise.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCurrentTheme(context: Context): Int {
        return getPreferences(context).getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }

    fun setTheme(context: Context, themeMode: Int) {
        getPreferences(context).edit()
            .putInt(KEY_THEME_MODE, themeMode)
            .apply()

        // Apply theme change immediately
        applyTheme(themeMode)
    }

    fun applyTheme(themeMode: Int) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun initializeTheme(context: Context) {
        val currentTheme = getCurrentTheme(context)
        applyTheme(currentTheme)
    }

    fun getThemeName(context: Context, themeMode: Int): String {
        return when (themeMode) {
            THEME_LIGHT -> "Light"
            THEME_DARK -> "Dark"
            THEME_SYSTEM -> "System"
            else -> "System"
        }
    }

    fun getNextTheme(currentTheme: Int): Int {
        return when (currentTheme) {
            THEME_LIGHT -> THEME_DARK
            THEME_DARK -> THEME_SYSTEM
            THEME_SYSTEM -> THEME_LIGHT
            else -> THEME_LIGHT
        }
    }
}
