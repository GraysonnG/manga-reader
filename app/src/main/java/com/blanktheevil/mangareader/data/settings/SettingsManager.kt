package com.blanktheevil.mangareader.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.blanktheevil.mangareader.data.ContentFilter
import com.blanktheevil.mangareader.data.ContentRating
import com.blanktheevil.mangareader.data.ReaderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultContentRatings: ContentRatings = listOf(
    ContentFilter.SAFE,
    ContentFilter.SUGGESTIVE,
)

typealias ContentRatings = List<ContentRating>

class SettingsManager(
    context: Context
) {
    private val settingsScope = CoroutineScope(Dispatchers.Main)
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        SETTINGS_KEY,
        Context.MODE_PRIVATE
    )
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
            val darkMode = sharedPreferences.getString("dark_mode", "system")!!
            val theme = sharedPreferences.getString("theme", "purple")!!

            notifyThemeChangedListener(
                darkMode,
                theme
            )
        }
    private var themeChangedListener: (darkMode: String, theme: String) -> Unit = { _, _ -> }

    init {
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
        private const val SETTINGS_KEY = "settings"
    }
}