package com.blanktheevil.mangareader.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsManager private constructor() {
    private var themeChangedListener: (darkMode: String, theme: String) -> Unit = { _,_ ->}
    private lateinit var sharedPrefs: SharedPreferences

    var darkMode
        get() = sharedPrefs.getString("dark_mode", "system")!!
        set(value) {
            CoroutineScope(Dispatchers.Main).launch {
                sharedPrefs.edit().putString(
                    "dark_mode",
                    value
                ).apply()
            }
        }

    var theme
        get() = sharedPrefs.getString("theme", "purple")!!
        set(value) {
            CoroutineScope(Dispatchers.Main).launch {
                sharedPrefs.edit().putString(
                    "theme",
                    value
                ).apply()
            }
        }

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(
            SETTINGS_KEY,
            Context.MODE_PRIVATE
        )
        CoroutineScope(Dispatchers.Main).launch {
            sharedPrefs.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                val darkMode = sharedPreferences.getString("dark_mode", "system")!!
                val theme = sharedPreferences.getString("theme", "purple")!!

                notifyThemeChangedListener(
                    darkMode,
                    theme
                )
            }

            val darkMode = sharedPrefs.getString("dark_mode", "system")!!
            val theme = sharedPrefs.getString("theme", "purple")!!

            notifyThemeChangedListener(
                darkMode,
                theme
            )
        }
    }

    fun addThemeChangedListener(
        onThemeChangedListener: (darkMode: String, theme: String) -> Unit
    ) {
        themeChangedListener = onThemeChangedListener
    }

    private fun notifyThemeChangedListener(
        darkMode: String,
        theme: String
    ) {
        themeChangedListener(
            darkMode,
            theme
        )
    }

    companion object {
        private var instance: SettingsManager? = null
        private const val SETTINGS_KEY = "settings"

        fun getInstance(): SettingsManager {
            if (instance == null) {
                instance = SettingsManager()
            }

            return instance!!
        }
    }
}