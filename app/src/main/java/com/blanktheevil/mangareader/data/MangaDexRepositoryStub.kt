package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.session.Session

class MangaDexRepositoryStub : MangaDexRepository {
    override suspend fun login(username: String, password: String): Result<Session> =
        success(StubData.SESSION)

    override suspend fun logout() {}

    override suspend fun getSession(): Session? = StubData.SESSION

    override suspend fun getManga(mangaId: String): Result<Manga> =
        success(StubData.Responses.GET_MANGA.data.toManga())

    override suspend fun getMangaList(
        name: String,
        mangaIds: List<String>
    ): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaSearch(
        query: String,
        limit: Int,
        offset: Int
    ): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaRecent(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaSeasonal(): Result<TitledMangaList> =
        success(
            TitledMangaList("Seasonal", StubData.MANGA_LIST.toMangaList())
        )

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaAggregate(mangaId: String): Result<Volumes> =
        success(emptyList())

    override suspend fun getChapter(chapterId: String): Result<Chapter> =
        success(StubData.Responses.GET_CHAPTER.data.toChapter())

    override suspend fun getChapterPages(
        chapterId: String,
        dataSaver: Boolean
    ): Result<List<String>> =
        success(StubData.Responses.GET_CHAPTER_PAGES.convertDataToUrl(dataSaver))

    override suspend fun getChapterList(
        ids: List<String>,
        limit: Int,
        offset: Int
    ): Result<ChapterList> =
        success(StubData.CHAPTER_LIST.toChapterList())

    override suspend fun getChapterListFollows(
        limit: Int,
        offset: Int
    ): Result<UpdatedChapterList> =
        success(
            UpdatedChapterList(
                total = 99,
                data = StubData.FEED_MAP,
            )
        )

    override suspend fun setChapterReadMarker(
        mangaId: String,
        chapterId: String,
        read: Boolean
    ): Result<Any> = success(Unit)

    override suspend fun getChapterReadMarkersForManga(
        mangaId: String
    ): Result<List<String>> =
        success(emptyList())

    override suspend fun getChapterReadMarkersForManga(
        mangaIds: List<String>
    ): Result<List<String>> =
        success(emptyList())

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