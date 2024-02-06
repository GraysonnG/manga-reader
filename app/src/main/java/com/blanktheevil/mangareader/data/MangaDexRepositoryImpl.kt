package com.blanktheevil.mangareader.data

import android.util.Log
import com.auth0.android.jwt.JWT
import com.blanktheevil.mangareader.VolumeData
import com.blanktheevil.mangareader.api.GithubApi
import com.blanktheevil.mangareader.api.MangaDexApi
import com.blanktheevil.mangareader.data.dto.AuthData
import com.blanktheevil.mangareader.data.dto.AuthTokenDto
import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.dto.TagList
import com.blanktheevil.mangareader.data.dto.toTagList
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.BaseModel
import com.blanktheevil.mangareader.data.room.models.MangaListType
import com.blanktheevil.mangareader.data.room.models.toModel
import com.blanktheevil.mangareader.data.session.Refresh
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.data.settings.ContentRatings
import com.blanktheevil.mangareader.toVolumeMap
import com.blanktheevil.mangareader.ui.SORT_MAP
import com.blanktheevil.mangareader.viewmodels.UPDATES_PAGE_SIZE
import com.squareup.moshi.Moshi
import java.time.Instant
import java.util.Date

class MangaDexRepositoryImpl(
    private val mangaDexApi: MangaDexApi,
    private val githubApi: GithubApi,
    private val sessionManager: SessionManager,
    private val historyManager: HistoryManager,
    private val mangaDao: MangaDao,
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

    override suspend fun isLoggedIn(): Result<Boolean> {
        val session = getSession()

        return when {
            session == null -> success(false)
            session.isExpired() -> success(false)
            !session.isExpired() -> success(true)
            else -> success(false)
        }
    }

    override suspend fun getManga(mangaId: String): Result<Manga> =
        makeCall(
            getLocalData = { mangaDao.getManga(mangaId) },
            setLocalData = { mangaDao.insertManga(it) }
        ) {
            mangaDexApi.getMangaById(id = mangaId).data.toManga()
        }

    override suspend fun getMangaList(
        name: String,
        mangaIds: List<String>
    ): Result<DataList<Manga>> =
        if (mangaIds.isNotEmpty()) {
            makeCall(
                getLocalData = { mangaDao.getMangaList(name) },
                setLocalData = { mangaDao.insertList(name, it) }
            ) {
                mangaDexApi.getManga(ids = mangaIds).toDataList()
            }
        } else
            error(Exception("No manga ids provided"))

    override suspend fun getMangaSearch(
        limit: Int,
        offset: Int,
        title: String,
        contentRating: ContentRatings,
        order: Pair<String, String>?,
        publicationDemographic: List<String>?,
        status: List<String>?,
        includedTags: List<String>?,
        excludedTags: List<String>?,
        includedTagsMode: TagsMode?,
        excludedTagsMode: TagsMode?,
        authors: List<String>?,
        artists: List<String>?,
        year: String?
    ): Result<DataList<Manga>> =
        makeCall(
            getLocalData = {
                mangaDao.getMangaList(
                    listOf(
                        MangaListType.SEARCH,
                        limit,
                        offset,
                        title,
                        contentRating,
                        order,
                        publicationDemographic,
                        status,
                        includedTags,
                        excludedTags,
                        includedTagsMode,
                        excludedTagsMode,
                        authors,
                        artists,
                        year
                    ).joinToString(",")
                )
            },
            setLocalData = { data ->
                val expiredSearches = mangaDao.getSearchLists()
                    .filter { it.isExpired() }
                    .map { it.key }
                mangaDao.clearLists(expiredSearches)

                mangaDao.insertList(
                    data.toModel(
                        listOf(
                            MangaListType.SEARCH,
                            limit,
                            offset,
                            title,
                            contentRating,
                            order,
                            publicationDemographic,
                            status,
                            includedTags,
                            excludedTags,
                            includedTagsMode,
                            excludedTagsMode,
                            authors,
                            artists,
                            year
                        ).joinToString(",")
                    )
                )
            }
        ) {
            mangaDexApi.getMangaSearch(
                limit = limit,
                offset = offset,
                contentRating = contentRating,
                order = order.toOrder(),
                title = title,
                publicationDemographic = publicationDemographic,
                status = status,
                includedTags = includedTags,
                excludedTags = excludedTags,
                includedTagsMode = includedTagsMode,
                excludedTagsMode = excludedTagsMode,
                authors = authors,
                artists = artists,
                year = year,
            ).toDataList()
        }

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeCall(
            getLocalData = {
                mangaDao.getMangaList("${MangaListType.POPULAR},$limit,$offset")
            },
            setLocalData = {
                mangaDao.insertList("${MangaListType.POPULAR},$limit,$offset", it)
            }
        ) {
            mangaDexApi.getMangaPopular(
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaSeasonal(): Result<TitledMangaList> =
        makeCall {
            val seasonalData = githubApi.getSeasonalData()
            val mangaList = getMangaList(
                name = MangaListType.SEASONAL.toString(),
                mangaIds = seasonalData.mangaIds
            ).collectOrDefault(DataList(items = emptyList()))
            TitledMangaList(
                seasonalData.name ?: "Seasonal",
                mangaList.items
            )
        }

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeAuthenticatedCall(
            getLocalData = {
                mangaDao.getMangaList("${MangaListType.FOLLOWS},$limit,$offset")
            },
            setLocalData = {
                mangaDao.insertList("${MangaListType.FOLLOWS},$limit,$offset", it)
            }
        ) { authorization ->
            mangaDexApi.getFollowsList(
                authorization = authorization,
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaRecent(limit: Int, offset: Int): Result<DataList<Manga>> =
        makeCall(
            getLocalData = {
                mangaDao.getMangaList("${MangaListType.RECENT},$limit,$offset")
            },
            setLocalData = {
                mangaDao.insertList("${MangaListType.RECENT},$limit,$offset", it)
            }
        ) {
            mangaDexApi.getMangaRecent(
                limit = limit,
                offset = offset,
            ).toDataList()
        }

    override suspend fun getMangaAggregate(mangaId: String): Result<Volumes> =
        makeCall {
            mangaDexApi.getMangaAggregate(id = mangaId).toVolumes()
        }

    override suspend fun getMangaFeed(
        id: String,
        limit: Int,
        offset: Int,
        authenticated: Boolean,
    ): Result<VolumeData> =
        if (authenticated) {
            makeAuthenticatedCall { authorization ->
                val chapters = mangaDexApi.getMangaChapters(
                    id = id,
                    limit = limit,
                    offset = offset,
                )

                val readMarkers = mangaDexApi.getReadChapterIdsByMangaIds(
                    authorization = authorization,
                    ids = listOf(id)
                ).data

                val volumes = chapters.data.toChapterList(
                    moshi = moshi,
                    readIds = readMarkers
                )
                    .toVolumeMap()

                VolumeData(
                    volumes = volumes,
                    totalChapters = chapters.total
                )
            }
        } else {
            makeCall {
                val chapters = mangaDexApi.getMangaChapters(
                    id = id,
                    limit = limit,
                    offset = offset,
                )

                val volumes = chapters.data
                    .toChapterList(moshi = moshi)
                    .toVolumeMap()

                VolumeData(
                    volumes = volumes,
                    totalChapters = chapters.total
                )
            }
        }

    override suspend fun getMangaCovers(id: String): Result<Map<String, String>> =
        makeCall {
            mangaDexApi.getMangaCovers(
                manga = listOf(id)
            ).data
                .associate {
                    it.attributes.volume to "$MANGADEX_COVER_URL/$id/${it.attributes.fileName}.512.jpg"
                }
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
        key: String,
        ids: List<String>,
        limit: Int,
        offset: Int,
    ): Result<ChapterList> =
        if (ids.isNotEmpty())
            makeCall() {
                mangaDexApi.getChapterList(ids = ids)
                    .data.toChapterList(moshi = moshi)
            }
        else
            error(Exception("No chapter ids provided"))

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

            val page = (response.offset / UPDATES_PAGE_SIZE) + 1

            mangaDao.clearList("chapter_follows_page_$page")

            response.data.toChapterList(moshi = moshi).let { list ->
                val mangaIds = list.mapNotNull { it.relatedMangaId }
                    .distinct()
                val relatedManga = getMangaList("chapter_follows_page_$page", mangaIds = mangaIds)
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
            error(Exception("No manga ids provided"))

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

    override suspend fun getTags(): Result<TagList> =
        makeCall {
            mangaDexApi.getAllTags().toTagList()
        }

    override suspend fun getAuthorList(
        name: String,
        limit: Int
    ): Result<List<Author>> =
        makeCall {
            mangaDexApi.getAuthorList(name = name, limit = limit)
                .data.map { it.toAuthor() }
        }

    private suspend fun refreshIfInvalid(session: Session?): Session? {
        session?.let {
            return@refreshIfInvalid if (it.isExpired()) {
                try {
                    val res = mangaDexApi.authRefresh(Refresh(it.refresh))
                    val newSession = createSession(res.token)
                    sessionManager.session = newSession
                    return newSession
                } catch (e: Exception) {
                    logout()
                    return null
                }
            } else {
                it
            }
        }

        throw SessionManager.InvalidSessionException("No Session Found!")
    }

    private suspend fun <T> makeCall(
        callback: suspend () -> T,
    ) = makeCall(
        getLocalData = null,
        setLocalData = null,
        callback = callback,
    )

    private suspend fun <T> makeCall(
        getLocalData: (suspend () -> BaseModel<T>?)?,
        setLocalData: (suspend (T) -> Unit)?,
        callback: suspend () -> T,
    ): Result<T> {
        return try {
            val localData = getLocalData?.invoke()

            if (localData != null && !localData.isExpired())
                return success(localData.data)

            val response = callback()

            setLocalData?.invoke(response)

            success(response)
        } catch (e: Exception) {
            Log.e("Repo", e.message.toString())
            e.printStackTrace()
            error(e)
        }
    }

    private suspend fun <T> makeAuthenticatedCall(
        callback: suspend (authorization: String) -> T,
    ) = makeAuthenticatedCall(
        getLocalData = null,
        setLocalData = null,
        callback = callback,
    )

    private suspend fun <T> makeAuthenticatedCall(
        getLocalData: (suspend () -> BaseModel<T>?)?,
        setLocalData: (suspend (T) -> Unit)?,
        callback: suspend (authorization: String) -> T
    ): Result<T> {
        val session = getSession()
        return if (session != null) {
            try {
                val localData = getLocalData?.invoke()

                if (localData != null && !localData.isExpired())
                    return success(localData.data)

                val validSession = refreshIfInvalid(session)
                    ?: return error(SessionManager.InvalidSessionException("Session was null"))

                val auth = "Bearer ${validSession.token}"
                val response = callback(auth)

                setLocalData?.invoke(response)

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

    private fun createSession(token: AuthTokenDto) = Session(
        token = token.session,
        refresh = token.refresh,
        expires = Date.from(Instant.now().plusMillis(FIFTEEN_MINUTES))
    )

    private fun Pair<String, String>?.toOrder(): Map<String, String> {
        if (this == null) return emptyMap()

        val map = mutableMapOf<String, String>()

        if (this != SORT_MAP.values.elementAt(0)) {
            map["order[${this.first}]"] = this.second
        }

        return map
    }

    companion object {
        private const val FIFTEEN_MINUTES: Long = 15 * 60000
        private const val MANGADEX_COVER_URL = "https://uploads.mangadex.org/covers"
    }
}