package com.blanktheevil.mangareader.data

import android.content.Context
import com.auth0.android.jwt.JWT
import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.dto.UserDto
import com.blanktheevil.mangareader.data.dto.getChapters
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

    suspend fun getUserFollowsList(
        limit: Int = 10,
        offset: Int = 0,
    ): Result<GetMangaListResponse> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.getFollowsList(
                    authorization = authorization,
                    limit = limit,
                    offset = offset
                )
            }
            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getUserFollowsChapterList(): Result<List<ChapterDto>> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.getFollowsChapterFeed(authorization = authorization)
            }

            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getPopularMangaList(
        limit: Int = 20,
        offset: Int = 0,
    ): Result<GetMangaListResponse> {
        return try {
            val res = mangaDexApi.getMangaPopular(
                limit = limit,
                offset = offset
            )
            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaList(ids: List<String>): Result<List<MangaDto>> {
        return try {
            val res = mangaDexApi.getManga(ids = ids, limit = ids.size)

            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaSearch(searchString: String): Result<List<MangaDto>> {
        return try {
            val res = mangaDexApi.getMangaSearch(title = searchString)

            Result.Success(res.data)
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

    suspend fun getMangaAggregateChapters(id: String): Result<Map<String, AggregateChapterDto>> {
        return try {
            val res = mangaDexApi.getMangaVolumesAndChapters(id = id)
            Result.Success(res.getChapters())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getChapterPages(chapterId: String): Result<List<String>> {
        return try {
            val res = mangaDexApi.getChapterPages(chapterId)
            val data = res.chapter.data?.map {
                "${res.baseUrl}/data/${res.chapter.hash}/$it"
            } ?: emptyList()
            Result.Success(data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getReadChapterIdsByMangaIds(mangaIds: List<String>): Result<List<String>> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.getReadChapterIdsByMangaIds(
                    authorization = authorization,
                    ids = mangaIds
                )
            }

            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun markChapterAsRead(
        mangaId: String,
        chapterId: String
    ): Result<Unit> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.markChapterRead(
                    id = mangaId,
                    authorization = authorization,
                    body = MarkChapterReadRequest(
                        chapterIdsRead = listOf(chapterId),
                        chapterIdsUnread = emptyList(),
                    ),
                )
            }

            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getChapterById(id: String): Result<ChapterDto> {
        return try {
            val res = mangaDexApi.getChapter(id)
            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    fun getUserId(): Result<String> {
        getSession()?.token?.let {
            val uid = JWT(it).getClaim("uid").asString() ?: return Result.Error(Exception("yikes"))

            return Result.Success(uid)
        }
        return Result.Error(InvalidSessionException("No session found!"))
    }

    suspend fun getUserData(id: String): Result<UserDto> {
        return try {
            val res = mangaDexApi.getUserInfo(id = id)
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

    private suspend fun <T> doAuthenticatedCall(
        callback: suspend (authorization: String, ) -> T
    ): T {
        if (getSession() != null) {
            refreshIfInvalid()
            val auth = "Bearer ${getSession()!!.token}"
            return callback(auth)
        } else {
            throw InvalidSessionException("No Session Found!")
        }
    }

    private fun createSession(token: AuthTokenDto) = Session(
        token = token.session,
        refresh = token.refresh,
        expires = Date.from(Instant.now().plusMillis(FIFTEEN_MINUTES))
    )

    companion object {
        private val instance = MangaDexRepository()

        fun getInstance(context: Context) : MangaDexRepository {
            return instance.also {
                if (it.sessionManager == null) {
                    it.initSessionManager(context)
                }
            }
        }
    }
}