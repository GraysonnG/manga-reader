package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.session.Session

interface MangaDexRepository {
    // auth
    suspend fun login(
        username: String,
        password: String,
    ): Result<Session>

    suspend fun logout()
    suspend fun getSession(): Session?

    // manga and manga lists
    suspend fun getManga(mangaId: String): Result<Manga>
    suspend fun getMangaList(name: String, mangaIds: List<String>): Result<DataList<Manga>>
    suspend fun getMangaSearch(
        query: String,
        limit: Int = 5,
        offset: Int = 0,
    ): Result<DataList<Manga>>

    suspend fun getMangaPopular(
        limit: Int = 20,
        offset: Int = 0,
    ): Result<DataList<Manga>>

    suspend fun getMangaRecent(
        limit: Int = 20,
        offset: Int = 0,
    ): Result<DataList<Manga>>

    suspend fun getMangaFollows(
        limit: Int = 20,
        offset: Int = 0,
    ): Result<DataList<Manga>>

    suspend fun getMangaSeasonal(): Result<TitledMangaList>

    // aggregate
    suspend fun getMangaAggregate(
        mangaId: String
    ): Result<Volumes>

    // chapter and chapter lists
    suspend fun getChapter(chapterId: String): Result<Chapter>
    suspend fun getChapterPages(
        chapterId: String,
        dataSaver: Boolean,
    ): Result<List<String>>

    suspend fun getChapterList(
        ids: List<String>,
        limit: Int = 20,
        offset: Int = 0,
    ): Result<ChapterList>

    suspend fun getChapterListFollows(
        limit: Int = 15,
        offset: Int = 0
    ): Result<UpdatedChapterList>

    // read markers
    suspend fun setChapterReadMarker(
        mangaId: String,
        chapterId: String,
        read: Boolean = true,
    ): Result<Any>

    suspend fun getChapterReadMarkersForManga(mangaId: String): Result<List<String>>
    suspend fun getChapterReadMarkersForManga(mangaIds: List<String>): Result<List<String>>

    // following
    suspend fun getMangaFollowed(mangaId: String): Result<Any>
    suspend fun setMangaFollowed(
        mangaId: String,
        followed: Boolean = true,
    ): Result<Any>

    // custom lists
    suspend fun getCustomLists(): Result<GetUserListsResponse>
    suspend fun addMangaToList(
        mangaId: String,
        listId: String,
    ): Result<Any>

    suspend fun removeMangaFromList(
        mangaId: String,
        listId: String,
    ): Result<Any>

    // user
    suspend fun getUserData(userId: String): Result<GetUserResponse>
    suspend fun getCurrentUserId(): Result<String>

    //history
    fun insertItemInHistory(
        mangaId: String,
        chapterId: String,
    )
}