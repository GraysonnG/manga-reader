package com.blanktheevil.mangareader.data

import com.auth0.android.jwt.JWT
import com.blanktheevil.mangareader.data.dto.AuthData
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.session.Refresh
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.session.SessionManager
import com.squareup.moshi.Moshi
import java.time.Instant
import java.util.Date

class MangaDexRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val githubApi: GithubApi,
    private val sessionManager: SessionManager,
    private val historyManager: HistoryManager,
    private val moshi: Moshi,
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

    override suspend fun getManga(mangaId: String): Result<Manga> =
        makeCall {
            mangaDexApi.getMangaById(id = mangaId).data.toManga()
        }

    override suspend fun getMangaList(mangaIds: List<String>): Result<DataList<Manga>> =
        if (mangaIds.isNotEmpty())
            makeCall {
                mangaDexApi.getManga(ids = mangaIds).toDataList()
            }
        else
            error(Exception("No manga ids provided"))

    override suspend fun getMangaSearch(
        query: String,
        limit: Int,
        offset: Int
    ): Result<DataList<Manga>> =
        if (query.isNotEmpty())
            makeCall {
                mangaDexApi.getMangaSearch(
                    title = query,
                    limit = limit,
                    offset = offset,
                ).toDataList()
            }
        else
            error(Exception("No query provided"))

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeCall {
            mangaDexApi.getMangaPopular(
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaSeasonal(): Result<TitledMangaList> =
        makeCall {
            val seasonalData = githubApi.getSeasonalData()
            val mangaList = getMangaList(
                mangaIds = seasonalData.mangaIds
            ).collectOrDefault(DataList(items = emptyList()))
            TitledMangaList(
                seasonalData.name ?: "Seasonal",
                mangaList.items
            )
        }

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeAuthenticatedCall { authorization ->
            mangaDexApi.getFollowsList(
                authorization = authorization,
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaRecent(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeCall {
            mangaDexApi.getMangaRecent(
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaAggregate(mangaId: String): Result<Volumes> =
        makeCall {
            mangaDexApi.getMangaAggregate(id = mangaId).toVolumes()
        }

    override suspend fun getChapter(chapterId: String): Result<Chapter> =
        makeCall {
            mangaDexApi.getChapter(id = chapterId).data.toChapter(
                moshi = moshi
            )
        }

    override suspend fun getChapterPages(
        chapterId: String,
        dataSaver: Boolean,
    ): Result<List<String>> =
        makeCall {
            mangaDexApi
                .getChapterPages(chapterId = chapterId)
                .convertDataToUrl(dataSaver = dataSaver)
        }

    override suspend fun getChapterList(
        ids: List<String>,
        limit: Int,
        offset: Int,
    ): Result<ChapterList> =
        if (ids.isNotEmpty())
            makeCall {
                mangaDexApi.getChapterList(ids = ids)
                    .data.toChapterList(moshi = moshi)
            }
        else
            Result.Error(Exception("No chapter ids provided"))

    override suspend fun getChapterListFollows(
        limit: Int,
        offset: Int
    ): Result<UpdatedChapterList> =
        makeAuthenticatedCall { authorization ->
            val response = mangaDexApi.getFollowsChapterFeed(
                authorization = authorization,
                limit = limit,
                offset = offset,
            )

            response.data.toChapterList(moshi = moshi).let { list ->
                val mangaIds = list.mapNotNull { it.relatedMangaId }
                    .distinct()
                val relatedManga = getMangaList(mangaIds = mangaIds)
                    .collectOrDefault(DataList(items = emptyList()))
                val readMarkers = getChapterReadMarkersForManga(mangaIds = mangaIds)
                    .collectOrEmpty()
                val chapters = list.map {
                    it.copy(
                        isRead = it.id in readMarkers,
                        relatedManga = relatedManga.items.firstOrNull { manga ->
                            it.relatedMangaId == manga.id
                        }
                    )
                }

                UpdatedChapterList(
                    total = response.total,
                    data = relatedManga.items.associateWith { manga ->
                        chapters.filter { c -> c.relatedManga?.id == manga.id }
                    }
                )
            }
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
    ): Result<List<String>> =
        getChapterReadMarkersForManga(listOf(mangaId))

    override suspend fun getChapterReadMarkersForManga(
        mangaIds: List<String>
    ): Result<List<String>> =
        if (mangaIds.isNotEmpty())
            makeAuthenticatedCall { authorization ->
                mangaDexApi.getReadChapterIdsByMangaIds(
                    authorization = authorization,
                    ids = mangaIds,
                ).data
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
            success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            error(e)
        }
    }

    private suspend fun <T> makeAuthenticatedCall(
        callback: suspend (authorization: String) -> T
    ): Result<T> {
        val session = getSession()
        return if (session != null) {
            try {
                val validSession = refreshIfInvalid(session)
                val auth = "Bearer ${validSession.token}"
                val response = callback(auth)
                success(response)
            } catch (e: Exception) {
                e.printStackTrace()
                error(e)
            }
        } else {
            error(
                SessionManager.InvalidSessionException("No Session Found!")
            )
        }
    }

    private suspend fun <T> makeAuthCall(
        callback: suspend (authorization: String) -> T
    ): Result<T> = try {
        val auth = "Bearer YOUR-API-KEY"
        val response = callback(auth)
        success(response)
    } catch (e: Exception) {
        e.printStackTrace()
        error(e)
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