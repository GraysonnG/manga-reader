package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.TagList
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.settings.ContentRatings
import com.blanktheevil.mangareader.data.settings.defaultContentRatings
import com.blanktheevil.mangareader.ui.SORT_MAP

interface MangaDexRepository {
    // auth
    suspend fun login(
        username: String,
        password: String,
    ): Result<Session>

    suspend fun logout()
    suspend fun getSession(): Session?
    suspend fun isLoggedIn(): Result<Boolean>

    // manga and manga lists
    suspend fun getManga(mangaId: String): Result<Manga>
    suspend fun getMangaList(name: String, mangaIds: List<String>): Result<DataList<Manga>>
    suspend fun getMangaSearch(
        limit: Int = 5,
        offset: Int = 0,
        title: String,
        contentRating: ContentRatings = defaultContentRatings,
        order: Pair<String, String>? = SORT_MAP.values.elementAt(1),
        publicationDemographic: List<String>? = null,
        status: List<String>? = null,
        includedTags: List<String>? = null,
        excludedTags: List<String>? = null,
        includedTagsMode: TagsMode? = null,
        excludedTagsMode: TagsMode? = null,
        authors: List<String>? = null,
        artists: List<String>? = null,
        year: String? = null,
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
        key: String,
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

    //tags
    suspend fun getTags(): Result<TagList>

    //author
    suspend fun getAuthorList(
        name: String,
        limit: Int,
    ): Result<List<Author>>
}