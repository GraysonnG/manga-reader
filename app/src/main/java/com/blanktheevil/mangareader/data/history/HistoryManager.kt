package com.blanktheevil.mangareader.data.history

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.Date

class HistoryManager private constructor(
    moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
) {
    private lateinit var sharedPrefs: SharedPreferences
    private val adapter = moshi.adapter(History::class.java)

    var history: History
        get() {
            val historyJson = sharedPrefs.getString(HISTORY_KEY, null)
            return if (historyJson == null) {
                History()
            } else {
                adapter.fromJson(historyJson) ?: History()
            }
        }
        set(value) {
            val historyJson = adapter.toJson(value)
            sharedPrefs.edit().putString(
                HISTORY_KEY,
                historyJson
            ).apply()
        }

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(
            HISTORY_KEY,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        private const val HISTORY_KEY = "history"
        private var instance: HistoryManager? = null

        fun getInstance(): HistoryManager {
            if (instance == null) {
                instance = HistoryManager()
            }
            return instance!!
        }
    }
}