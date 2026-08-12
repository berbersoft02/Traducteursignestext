package com.berbersoft.traducteursignestext.utils

import android.content.Context
import android.content.SharedPreferences

object ThemeHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_THEME = "selected_theme"

    const val THEME_DREAMY = "dreamy"
    const val THEME_CLASSIC_BLUE = "classic_blue"

    fun saveTheme(context: Context, theme: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    fun getTheme(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, THEME_DREAMY) ?: THEME_DREAMY
    }
}
