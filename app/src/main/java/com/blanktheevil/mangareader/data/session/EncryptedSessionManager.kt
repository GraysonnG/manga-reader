package com.blanktheevil.mangareader.data.session

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date


private const val SHARED_PREFERENCE_NAME = "bte_manga_reader"

class EncryptedSessionManager(
    private val context: Context,
    moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()
) : SessionManager {
    private var adapter = moshi.adapter(Session::class.java)
    private val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private var _session: Session? = null

    override var session: Session?
        get() {
            _session = getSessionFromSharedPreferences()
            _isLoggedIn.value = _session != null
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
            _isLoggedIn.value = _session != null
        }

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private fun getSessionFromSharedPreferences(): Session? {
        val sessionJson = getSharedPreferences().getString("session", null)
        return if (sessionJson != null) {
            adapter.fromJson(sessionJson)
        } else {
            null
        }
    }

    private fun getSharedPreferences() =
        EncryptedSharedPreferences.create(
            context,
            SHARED_PREFERENCE_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}