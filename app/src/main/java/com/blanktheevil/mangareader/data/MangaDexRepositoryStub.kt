package com.blanktheevil.mangareader.data

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
import com.blanktheevil.mangareader.data.session.Session

class MangaDexRepositoryStub : MangaDexRepository {
    override suspend fun login(username: String, password: String): Result<Session> =
        success(StubData.SESSION)

    override suspend fun logout() {}

    override suspend fun getSession(): Session? = StubData.SESSION

    override suspend fun getManga(mangaId: String): Result<GetMangaResponse> =
        success(StubData.Responses.GET_MANGA)

    override suspend fun getMangaList(mangaIds: List<String>): Result<GetMangaListResponse> =
        success(StubData.Responses.GET_MANGA_LIST)

    override suspend fun getMangaSearch(
        query: String,
        limit: Int,
        offset: Int
    ): Result<GetMangaListResponse> =
        success(StubData.Responses.GET_MANGA_LIST)

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<GetMangaListResponse> =
        success(StubData.Responses.GET_MANGA_LIST)

    override suspend fun getMangaRecent(limit: Int, offset: Int): Result<GetMangaListResponse> =
        success(StubData.Responses.GET_MANGA_LIST)

    override suspend fun getMangaSeasonal(): Result<GetSeasonalDataResponse> =
        success(StubData.Responses.GET_SEASONAL_DATA)

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<GetMangaListResponse> =
        success(StubData.Responses.GET_MANGA_LIST)

    override suspend fun getMangaAggregate(mangaId: String): Result<GetMangaAggregateResponse> =
        success(StubData.Responses.GET_MANGA_AGGREGATE)

    override suspend fun getChapter(chapterId: String): Result<GetChapterResponse> =
        success(StubData.Responses.GET_CHAPTER)

    override suspend fun getChapterPages(chapterId: String): Result<GetChapterPagesResponse> =
        success(StubData.Responses.GET_CHAPTER_PAGES)

    override suspend fun getChapterList(
        ids: List<String>,
        limit: Int,
        offset: Int
    ): Result<GetChapterListResponse> =
        success(StubData.Responses.GET_CHAPTER_LIST)

    override suspend fun getChapterListFollows(
        limit: Int,
        offset: Int
    ): Result<GetChapterListResponse> =
        success(StubData.Responses.GET_CHAPTER_LIST)

    override suspend fun setChapterReadMarker(
        mangaId: String,
        chapterId: String,
        read: Boolean
    ): Result<Any> = success(Unit)

    override suspend fun getChapterReadMarkersForManga(
        mangaId: String
    ): Result<GetChapterIdsResponse> =
        success(StubData.Responses.GET_CHAPTER_IDS)

    override suspend fun getChapterReadMarkersForManga(
        mangaIds: List<String>
    ): Result<GetChapterIdsResponse> =
        success(StubData.Responses.GET_CHAPTER_IDS)

    override suspend fun getMangaFollowed(mangaId: String): Result<Any> =
        success(Unit)

    override suspend fun setMangaFollowed(mangaId: String, followed: Boolean): Result<Any> =
        success(Unit)

    override suspend fun getCustomLists(): Result<GetUserListsResponse> =
        success(StubData.Responses.GET_USER_LISTS)

    override suspend fun addMangaToList(mangaId: String, listId: String): Result<Any> =
        success(Unit)

    override suspend fun removeMangaFromList(mangaId: String, listId: String): Result<Any> =
        success(Unit)

    override suspend fun getUserData(userId: String): Result<GetUserResponse> =
        success(StubData.Responses.GET_USER)

    override suspend fun getCurrentUserId(): Result<String> =
        success(StubData.Responses.GET_USER.data.id)

    override fun insertItemInHistory(mangaId: String, chapterId: String) {}
}