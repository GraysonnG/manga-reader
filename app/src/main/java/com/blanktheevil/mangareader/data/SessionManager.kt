package com.blanktheevil.mangareader.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.util.Date

private const val SHARED_PREFERENCE_NAME = "bte_manga_reader"

class SessionManager(
    private val context: Context,
    moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
) {
    private val adapter = moshi.adapter(Session::class.java)
    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private fun getSharedPreferences() =
        EncryptedSharedPreferences.create(
            context,
            SHARED_PREFERENCE_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveSession(session: Session) {
        val sessionJson = adapter.toJson(session)
        getSharedPreferences()
            .edit()
            .putString("session", sessionJson)
            .apply()
    }

    fun getSession(): Session? {
        val sessionJson = getSharedPreferences()
            .getString("session", null)
        return sessionJson?.let { adapter.fromJson(it) }
    }

    fun clearSession() {
        getSharedPreferences()
            .edit()
            .remove("session")
            .apply()
    }
}

class InvalidSessionException(message: String) : Exception(message)