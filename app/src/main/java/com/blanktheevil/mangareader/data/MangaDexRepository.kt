package com.blanktheevil.mangareader.data

import android.content.Context
import com.auth0.android.jwt.JWT
import com.blanktheevil.mangareader.ChapterList
import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterPagesDataDto
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.dto.UserDto
import com.blanktheevil.mangareader.data.dto.getChapters
import com.blanktheevil.mangareader.data.history.History
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.session.EncryptedSessionManager
import com.blanktheevil.mangareader.data.session.Refresh
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.data.settings.ContentRatings
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.data.settings.defaultContentRatings
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.time.Instant
import java.util.Date

private const val BASE_URL = "https://api.mangadex.org"
private const val FIFTEEN_MINUTES: Long = 15 * 60000

class MangaDexRepository {
    private val client = OkHttpClient.Builder().build()
    private val moshi = Moshi.Builder()
        .add(JSONObject::class.java, JSONObjectAdapter())
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .build()

    private val mangaDexApi: MangaDexApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()
        .create()

    private var sessionManager: SessionManager? = null
    private var settingsManager: SettingsManager? = null
    private var historyManager : HistoryManager? = null

    fun initRepositoryManagers(context: Context) {
        try {
            sessionManager = EncryptedSessionManager(context)
            settingsManager = SettingsManager.getInstance().apply {
                init(context)
            }
            historyManager = HistoryManager.getInstance().apply {
                init(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSession(): Session? {
        return sessionManager?.session
    }

    fun logout() {
        sessionManager?.session = null
    }

    fun addItemToHistory(mangaId: String, chapterId: String) {
        val history = historyManager?.history ?: History()
        val now = Date.from(Instant.now())

        history.items.getOrPut(mangaId) {
            HashMap()
        }[chapterId] = now

        historyManager?.history = history
    }

    suspend fun login(user: String, pass: String): Result<Session> {
        return try {
            val res = mangaDexApi.authLogin(AuthData(user, pass))
            val session = createSession(res.token)
            sessionManager?.session = session

            Result.Success(session)
        } catch (e: Exception) {
            e.printStackTrace()
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

    suspend fun getUserFollowsChapterList(
        limit: Int = 15,
        offset: Int = 0,
    ): Result<GetChapterListResponse> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.getFollowsChapterFeed(
                    limit = limit,
                    offset = offset,
                    authorization = authorization
                )
            }

            Result.Success(res)
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
            val contentRatings: ContentRatings =
                settingsManager?.contentFilters?.toList() ?: defaultContentRatings
            val res = mangaDexApi.getMangaPopular(
                contentRating = contentRatings,
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
            val contentRatings: ContentRatings =
                settingsManager?.contentFilters?.toList() ?: defaultContentRatings
            val res = mangaDexApi.getManga(
                contentRating = contentRatings,
                ids = ids,
                limit = ids.size
            )

            Result.Success(res.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaSearch(searchString: String): Result<List<MangaDto>> {
        return try {
            val contentRatings: ContentRatings =
                settingsManager?.contentFilters?.toList() ?: defaultContentRatings
            val res = mangaDexApi.getMangaSearch(
                contentRating = contentRatings,
                title = searchString
            )

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

    suspend fun getMangaAggregate(id: String): Result<Map<String, AggregateVolumeDto>> {
        return try {
            val res = mangaDexApi.getMangaAggregate(id)
            Result.Success(res.volumes)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getMangaAggregateChapters(id: String): Result<Map<String, AggregateChapterDto>> {
        return try {
            val res = mangaDexApi.getMangaAggregate(id = id)
            Result.Success(res.getChapters())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun getChapterPages(chapterId: String): Result<List<String>> {
        return try {
            val dataSaver = settingsManager?.dataSaver ?: false
            val res = mangaDexApi.getChapterPages(chapterId)
            val data = convertDataToUrl(res.baseUrl, dataSaver, res.chapter)

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

    suspend fun getChapterList(ids: List<String>): Result<ChapterList> {
        return try {
            val res = mangaDexApi.getChapterList(ids = ids)
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
        return Result.Error(SessionManager.InvalidSessionException("No session found!"))
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

    suspend fun getIsUserFollowingManga(mangaId: String): Result<Any> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.getIsUserFollowingManga(
                    authorization = authorization,
                    id = mangaId,
                )
            }
            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun setMangaFollowed(mangaId: String): Result<Any> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.followManga(
                    authorization = authorization,
                    id = mangaId,
                )
            }

            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    suspend fun setMangaUnfollowed(mangaId: String): Result<Any> {
        return try {
            val res = doAuthenticatedCall { authorization ->
                mangaDexApi.unfollowManga(
                    authorization = authorization,
                    id = mangaId,
                )
            }

            Result.Success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    private suspend fun refreshIfInvalid(session: Session?): Session {
        session?.let {
            return@refreshIfInvalid if (it.isExpired()) {
                val res = mangaDexApi.authRefresh(Refresh(it.refresh))
                val newSession = createSession(res.token)
                sessionManager?.session = newSession
                newSession
            } else {
                it
            }
        }

        throw SessionManager.InvalidSessionException("No Session Found!")
    }

    private suspend fun <T> doAuthenticatedCall(
        callback: suspend (authorization: String, ) -> T
    ): T {
        val session = getSession()
        if (session != null) {
            val validSession = refreshIfInvalid(session)
            val auth = "Bearer ${validSession.token}"
            return callback(auth)
        } else {
            throw SessionManager.InvalidSessionException("No Session Found!")
        }
    }

    private fun createSession(token: AuthTokenDto) = Session(
        token = token.session,
        refresh = token.refresh,
        expires = Date.from(Instant.now().plusMillis(FIFTEEN_MINUTES))
    )

    private fun convertDataToUrl(
        baseUrl: String,
        dataSaver: Boolean,
        data: ChapterPagesDataDto,
    ): List<String> {
        val imageQuality = if (dataSaver && data.dataSaver != null) "data-saver" else "data"
        return (if (dataSaver && data.dataSaver != null) data.dataSaver else data.data)?.map {
            "${baseUrl}/${imageQuality}/${data.hash}/$it"
        } ?: emptyList()
    }

    companion object {
        private val instance = MangaDexRepository()

        val DEFAULT_MOSHI = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

        fun getInstance(context: Context) : MangaDexRepository {
            return instance.also {
                if (it.sessionManager == null) {
                    it.initRepositoryManagers(context)
                }
            }
        }
    }
}