package com.blanktheevil.mangareader.data.history

import android.content.SharedPreferences
import com.squareup.moshi.Moshi

class HistoryManagerImpl(
    moshi: Moshi,
    private val sharedPrefs: SharedPreferences,
) : HistoryManager {
    private val adapter = moshi.adapter(History::class.java)

    override var history: History
        get() {
            val historyJson = sharedPrefs.getString(HistoryManager.HISTORY_KEY, null)
            return if (historyJson == null) {
                History()
            } else {
                adapter.fromJson(historyJson) ?: History()
            }
        }
        set(value) {
            val historyJson = adapter.toJson(value)
            sharedPrefs.edit().putString(
                HistoryManager.HISTORY_KEY,
                historyJson
            ).apply()
        }
}