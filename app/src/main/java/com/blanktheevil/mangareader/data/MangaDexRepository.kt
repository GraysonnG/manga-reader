package com.blanktheevil.mangareader.data

import android.content.Context
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.time.Instant
import java.util.*

private const val BASE_URL = "https://api.mangadex.org"
private const val FIFTEEN_MINUTES: Long = 15 * 60000

class MangaDexRepository {
    private val client = OkHttpClient.Builder().build()
    private val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    private val mangaDexApi: MangaDexApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()
        .create()

    private var sessionManager: SessionManager? = null

    fun initSessionManager(context: Context) {
        try {
            sessionManager = SessionManager(context, moshi)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSession(): Session? {
        return sessionManager?.getSession()
    }

    fun logout() {
        sessionManager?.clearSession()
    }

    suspend fun login(user: String, pass: String): Result<Session> {
        return try {
            val res = mangaDexApi.authLogin(AuthData(user, pass))
            val session = createSession(res.token)
            sessionManager?.saveSession(session)

            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    suspend fun getUserFollowsList(): Result<List<MangaDto>> {
        return try {
            if (getSession() != null) {
                refreshIfInvalid()
                val res = mangaDexApi.getFollowsList(
                    "Bearer ${getSession()!!.token}"
                )
                Result.Success(res.data)
            } else {
                Result.Error(InvalidSessionException("No Session Found!"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaDetails(id: String): Result<MangaDto> {
        return try {
            val res = mangaDexApi.getMangaById(id)
            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaChapters(id: String): Result<List<ChapterDto>> {
        return try {
            val res = mangaDexApi.getMangaChapters(id)
            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    private suspend fun refreshIfInvalid() {
        getSession()?.let {
            if (it.expires.time <= Date.from(Instant.now()).time) {
                val res = mangaDexApi.authRefresh(Refresh(it.refresh))
                sessionManager?.saveSession(createSession(res.token))
            }
        }
    }

    private fun createSession(token: AuthTokenDto) = Session(
        token = token.session,
        refresh = token.refresh,
        expires = Date.from(Instant.now().plusMillis(FIFTEEN_MINUTES))
    )
}