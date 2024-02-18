package com.blanktheevil.mangareader.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.blanktheevil.mangareader.data.ReaderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultContentRatings: ContentRatings = listOf(
    "safe",
    "suggestive",
)

typealias ContentRatings = List<String>

class SettingsManager private constructor() {
    private val settingsScope = CoroutineScope(Dispatchers.Main)
    private var themeChangedListener: (darkMode: String, theme: String) -> Unit = { _, _ -> }
    private lateinit var sharedPrefs: SharedPreferences
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
            val darkMode = sharedPreferences.getString("dark_mode", "system")!!
            val theme = sharedPreferences.getString("theme", "purple")!!

            notifyThemeChangedListener(
                darkMode,
                theme
            )
        }

    var darkMode
        get() = sharedPrefs.getString("dark_mode", "system")!!
        set(value) {
            settingsScope.launch {
                sharedPrefs.edit().putString(
                    "dark_mode",
                    value
                ).apply()
            }
        }

    var theme
        get() = sharedPrefs.getString("theme", "purple")!!
        set(value) {
            settingsScope.launch {
                sharedPrefs.edit().putString(
                    "theme",
                    value
                ).apply()
            }
        }

    var dataSaver
        get() = sharedPrefs.getBoolean("data_saver", false)
        set(value) {
            settingsScope.launch {
                sharedPrefs.edit().putBoolean(
                    "data_saver",
                    value
                ).apply()
            }
        }

    var contentFilters: Set<String>
        get() = sharedPrefs.getStringSet("content_filters", defaultContentRatings.toSet())!!
        set(value) {
            settingsScope.launch {
                sharedPrefs.edit().putStringSet(
                    "content_filters",
                    value
                ).apply()
            }
        }

    var readerType: ReaderType
        get() {
            val readerTypeName = sharedPrefs.getString("reader_type", ReaderType.PAGE.name)!!
            return ReaderType.valueOf(readerTypeName)
        }
        set(value) {
            settingsScope.launch {
                sharedPrefs.edit().putString(
                    "reader_type",
                    value.name
                ).apply()
            }
        }


    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(
            SETTINGS_KEY,
            Context.MODE_PRIVATE
        )
        settingsScope.launch {
            sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

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