package com.blanktheevil.mangareader.data.session

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

private const val SHARED_PREFERENCE_NAME = "bte_manga_reader"

//class EncryptedSessionManager(
//    private val context: Context,
//    moshi: Moshi = Moshi.Builder()
//        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
//        .build()
//): SessionManager {
//    private val adapter = moshi.adapter(Session::class.java)
//    private val masterKeyAlias = MasterKey.Builder(context)
//        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//        .build()
//
////    override var session: Session?
////        get() = getSession()
////        set(value) {
////            value?.let { saveSession(value) }
////        }
//
//
//    private fun getSharedPreferences() =
//        EncryptedSharedPreferences.create(
//            context,
//            SHARED_PREFERENCE_NAME,
//            masterKeyAlias,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//
//    override fun saveSession(session: Session) {
//        val sessionJson = adapter.toJson(session)
//        CoroutineScope(Dispatchers.Main).launch {
//            getSharedPreferences()
//                .edit()
//                .putString("session", sessionJson)
//                .apply()
//        }
//        currentSession = session
//    }
//
//    override fun getSession(): Session? {
//        return currentSession
//    }
//
//    override fun clearSession() {
//        CoroutineScope(Dispatchers.Main).launch {
//            getSharedPreferences()
//                .edit()
//                .remove("session")
//                .apply()
//            currentSession = null
//        }
//    }
//
//    private fun getSessionFromSharedPrefs(): Session? {
//        val sessionJson = getSharedPreferences()
//            .getString("session", null)
//        return sessionJson?.let { adapter.fromJson(it) }
//    }
//
//    companion object {
//        private var currentSession: Session? = null
//
//        fun initCurrentSession(context: Context) {
//            val manager = EncryptedSessionManager(context)
//            currentSession = manager.getSessionFromSharedPrefs()
//        }
//    }
//}