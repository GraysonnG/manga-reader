package com.blanktheevil.mangareader.data

import com.auth0.android.jwt.JWT
import com.blanktheevil.mangareader.data.dto.AuthData
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.GetChapterIdsResponse
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.GetChapterResponse
import com.blanktheevil.mangareader.data.dto.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.GetMangaResponse
import com.blanktheevil.mangareader.data.dto.GetSeasonalDataResponse
import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.session.Refresh
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.session.SessionManager
import java.time.Instant
import java.util.Date

class MangaDexRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val githubApi: GithubApi,
    private val sessionManager: SessionManager,
    private val historyManager: HistoryManager,
) : MangaDexRepository {
    override suspend fun login(username: String, password: String): Result<Session> =
        makeCall {
            val res = mangaDexApi.authLogin(AuthData(username, password))
            val session = createSession(res.token)
            sessionManager.session = session

            return@makeCall session
        }


    override suspend fun logout() {
        sessionManager.session = null
    }

    override suspend fun getSession(): Session? {
        return sessionManager.session
    }

    override suspend fun getManga(mangaId: String): Result<GetMangaResponse> =
        makeCall { mangaDexApi.getMangaById(id = mangaId) }

    override suspend fun getMangaList(mangaIds: List<String>): Result<GetMangaListResponse> =
        if (mangaIds.isNotEmpty())
            makeCall { mangaDexApi.getManga(ids = mangaIds) }
        else
            Result.Error(Exception("No manga ids provided"))

    override suspend fun getMangaSearch(
        query: String,
        limit: Int,
        offset: Int
    ): Result<GetMangaListResponse> =
        if (query.isNotEmpty())
            makeCall {
                mangaDexApi.getMangaSearch(
                    title = query,
                    limit = limit,
                    offset = offset,
                )
            }
        else
            Result.Error(Exception("No query provided"))

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<GetMangaListResponse> =
        makeCall {
            mangaDexApi.getMangaPopular(
                limit = limit,
                offset = offset,
            )
        }

    override suspend fun getMangaSeasonal(): Result<GetSeasonalDataResponse> =
        makeCall { githubApi.getSeasonalData() }

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<GetMangaListResponse> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.getFollowsList(
                authorization = authorization,
                limit = limit,
                offset = offset,
            )
        }

    override suspend fun getMangaAggregate(mangaId: String): Result<GetMangaAggregateResponse> =
        makeCall { mangaDexApi.getMangaAggregate(id = mangaId) }

    override suspend fun getChapter(chapterId: String): Result<GetChapterResponse> =
        makeCall { mangaDexApi.getChapter(id = chapterId) }

    override suspend fun getChapterPages(chapterId: String): Result<GetChapterPagesResponse> =
        makeCall { mangaDexApi.getChapterPages(chapterId = chapterId) }

    override suspend fun getChapterList(
        ids: List<String>,
        limit: Int,
        offset: Int,
    ): Result<GetChapterListResponse> =
        if (ids.isNotEmpty())
            makeCall { mangaDexApi.getChapterList(ids = ids) }
        else
            Result.Error(Exception("No chapter ids provided"))

    override suspend fun getChapterListFollows(
        limit: Int,
        offset: Int
    ): Result<GetChapterListResponse> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.getFollowsChapterFeed(
                authorization = authorization,
                limit = limit,
                offset = offset,
            )
        }

    override suspend fun setChapterReadMarker(
        mangaId: String,
        chapterId: String,
        read: Boolean
    ): Result<Any> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.markChapterRead(
                authorization = authorization,
                id = mangaId,
                body = MarkChapterReadRequest(
                    chapterIdsRead = if (read) listOf(chapterId) else emptyList(),
                    chapterIdsUnread = if (!read) listOf(chapterId) else emptyList(),
                ),
            )
        }

    override suspend fun getChapterReadMarkersForManga(
        mangaId: String
    ): Result<GetChapterIdsResponse> =
        getChapterReadMarkersForManga(listOf(mangaId))

    override suspend fun getChapterReadMarkersForManga(
        mangaIds: List<String>
    ): Result<GetChapterIdsResponse> =
        if (mangaIds.isNotEmpty())
            makeAuthenticatedCall { authorization ->
                mangaDexApi.getReadChapterIdsByMangaIds(
                    authorization = authorization,
                    ids = mangaIds,
                )
            }
        else
            Result.Error(Exception("No manga ids provided"))

    override suspend fun getMangaFollowed(mangaId: String): Result<Any> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.getIsUserFollowingManga(
                authorization = authorization,
                id = mangaId,
            )
        }

    override suspend fun setMangaFollowed(mangaId: String, followed: Boolean): Result<Any> =
        makeAuthenticatedCall {
            if (followed) {
                mangaDexApi.followManga(
                    authorization = it,
                    id = mangaId,
                )
            } else {
                mangaDexApi.unfollowManga(
                    authorization = it,
                    id = mangaId,
                )
            }
        }

    override suspend fun getCustomLists(): Result<GetUserListsResponse> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.getUserLists(
                authorization = authorization,
            )
        }

    override suspend fun addMangaToList(mangaId: String, listId: String): Result<Any> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.addMangaToList(
                authorization = authorization,
                id = mangaId,
                listId = listId,
            )
        }

    override suspend fun removeMangaFromList(mangaId: String, listId: String): Result<Any> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.removeMangaFromList(
                authorization = authorization,
                id = mangaId,
                listId = listId,
            )
        }

    override suspend fun getUserData(userId: String): Result<GetUserResponse> =
        makeCall { mangaDexApi.getUserInfo(id = userId) }

    override suspend fun getCurrentUserId(): Result<String> =
        makeCall {
            getSession()!!.token.let {
                JWT(it).getClaim("uid").asString()!!
            }
        }

    override fun insertItemInHistory(
        mangaId: String,
        chapterId: String
    ) {
        val history = historyManager.history
        val now = Date.from(Instant.now())

        history.items.getOrPut(mangaId) {
            HashMap()
        }[chapterId] = now

        historyManager.history = history
    }

    private suspend fun refreshIfInvalid(session: Session?): Session {
        session?.let {
            return@refreshIfInvalid if (it.isExpired()) {
                val res = mangaDexApi.authRefresh(Refresh(it.refresh))
                val newSession = createSession(res.token)
                sessionManager.session = newSession
                newSession
            } else {
                it
            }
        }

        throw SessionManager.InvalidSessionException("No Session Found!")
    }

    private suspend fun <T> makeCall(
        callback: suspend () -> T
    ): Result<T> {
        return try {
            val response = callback()
            Result.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    private suspend fun <T> makeAuthenticatedCall(
        callback: suspend (authorization: String) -> T
    ): Result<T> {
        val session = getSession()
        return if (session != null) {
            val validSession = refreshIfInvalid(session)
            val auth = "Bearer ${validSession.token}"
            try {
                val response = callback(auth)
                Result.Success(response)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(e)
            }
        } else {
            Result.Error(
                SessionManager.InvalidSessionException("No Session Found!")
            )
        }
    }

    private fun createSession(token: AuthTokenDto) = Session(
        token = token.session,
        refresh = token.refresh,
        expires = Date.from(Instant.now().plusMillis(FIFTEEN_MINUTES))
    )

    companion object {
        private const val FIFTEEN_MINUTES: Long = 15 * 60000
    }
}