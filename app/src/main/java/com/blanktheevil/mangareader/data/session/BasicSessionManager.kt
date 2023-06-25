package com.blanktheevil.mangareader.data.session

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.Date

private const val SHARED_PREFERENCE_NAME = "bte_manga_reader_unencrypted"

class BasicSessionManager(
    private val context: Context,
    moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
) : SessionManager {
    private val adapter = moshi.adapter(Session::class.java)

    private var _session: Session? = null

    override var session: Session?
        get() {
            _session = getSessionFromSharedPreferences()
            return _session
        }
        set(value) {
            _session = if (value != null) {
                val sessionJson = adapter.toJson(value)
                getSharedPreferences()
                    .edit()
                    .putString("session", sessionJson)
                    .apply()
                value
            } else {
                getSharedPreferences()
                    .edit()
                    .remove("session")
                    .apply()
                null
            }
        }

    private fun getSessionFromSharedPreferences(): Session? {
        val sessionJson = getSharedPreferences().getString("session", null)
        return if (sessionJson != null) {
            adapter.fromJson(sessionJson)
        } else {
            null
        }
    }

    private fun getSharedPreferences() =
        context.getSharedPreferences(
            SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
}