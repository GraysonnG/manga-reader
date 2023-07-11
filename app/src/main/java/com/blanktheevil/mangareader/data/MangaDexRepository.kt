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

interface MangaDexRepository {
    // auth
    suspend fun login(username: String, password: String): Result<Session>
    suspend fun logout()
    suspend fun getSession(): Session?

    // manga and manga lists
    suspend fun getManga(mangaId: String): Result<GetMangaResponse>
    suspend fun getMangaList(mangaIds: List<String>): Result<GetMangaListResponse>
    suspend fun getMangaSearch(
        query: String,
        limit: Int = 5,
        offset: Int = 0,
    ): Result<GetMangaListResponse>

    suspend fun getMangaPopular(
        limit: Int = 20,
        offset: Int = 0,
    ): Result<GetMangaListResponse>

    suspend fun getMangaSeasonal(): Result<GetSeasonalDataResponse>
    suspend fun getMangaFollows(
        limit: Int = 20,
        offset: Int = 0
    ): Result<GetMangaListResponse>

    // aggregate
    suspend fun getMangaAggregate(
        mangaId: String
    ): Result<GetMangaAggregateResponse>

    // chapter and chapter lists
    suspend fun getChapter(chapterId: String): Result<GetChapterResponse>
    suspend fun getChapterPages(chapterId: String): Result<GetChapterPagesResponse>
    suspend fun getChapterList(
        ids: List<String>,
        limit: Int = 20,
        offset: Int = 0,
    ): Result<GetChapterListResponse>

    suspend fun getChapterListFollows(
        limit: Int = 15,
        offset: Int = 0
    ): Result<GetChapterListResponse>

    // read markers
    suspend fun setChapterReadMarker(
        mangaId: String,
        chapterId: String,
        read: Boolean = true,
    ): Result<Any>

    suspend fun getChapterReadMarkersForManga(mangaId: String): Result<GetChapterIdsResponse>
    suspend fun getChapterReadMarkersForManga(mangaIds: List<String>): Result<GetChapterIdsResponse>

    // following
    suspend fun getMangaFollowed(mangaId: String): Result<Any>
    suspend fun setMangaFollowed(mangaId: String, followed: Boolean = true): Result<Any>

    // custom lists
    suspend fun getCustomLists(): Result<GetUserListsResponse>
    suspend fun addMangaToList(mangaId: String, listId: String): Result<Any>
    suspend fun removeMangaFromList(mangaId: String, listId: String): Result<Any>

    // user
    suspend fun getUserData(userId: String): Result<GetUserResponse>
    suspend fun getCurrentUserId(): Result<String>

    //history
    fun insertItemInHistory(
        mangaId: String,
        chapterId: String,
    )
}