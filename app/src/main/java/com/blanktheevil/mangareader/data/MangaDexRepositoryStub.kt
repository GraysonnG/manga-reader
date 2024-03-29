package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.VolumeData
import com.blanktheevil.mangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.responses.GetUserResponse
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import com.blanktheevil.mangareader.data.dto.utils.DataList
import com.blanktheevil.mangareader.data.dto.utils.TagList
import com.blanktheevil.mangareader.data.dto.utils.Volumes
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapter
import com.blanktheevil.mangareader.data.dto.utils.chapter.toChapterList
import com.blanktheevil.mangareader.data.dto.utils.convertDataToUrl
import com.blanktheevil.mangareader.data.dto.utils.manga.TitledMangaList
import com.blanktheevil.mangareader.data.dto.utils.manga.toDataList
import com.blanktheevil.mangareader.data.dto.utils.manga.toManga
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.data.session.Session
import com.blanktheevil.mangareader.data.settings.ContentRatings
import com.blanktheevil.mangareader.toVolumeMap

class MangaDexRepositoryStub : MangaDexRepository {
    override suspend fun login(username: String, password: String): Result<Session> =
        success(StubData.Data.SESSION)

    override suspend fun logout() {}

    override suspend fun getSession(): Session = StubData.Data.SESSION

    override suspend fun isLoggedIn(): Result<Boolean> = success(true)

    override suspend fun getManga(mangaId: String): Result<Manga> =
        success(StubData.Responses.GET_MANGA.data.toManga())

    override suspend fun getMangaList(
        name: String,
        mangaIds: List<String>
    ): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

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
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaPopular(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaRecent(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaSeasonal(): Result<TitledMangaList> =
        success(
            TitledMangaList("Seasonal", StubData.Data.MANGA_LIST.toMangaList())
        )

    override suspend fun getMangaFollows(limit: Int, offset: Int): Result<DataList<Manga>> =
        success(StubData.Responses.GET_MANGA_LIST.toDataList())

    override suspend fun getMangaAggregate(mangaId: String): Result<Volumes> =
        success(emptyList())

    override suspend fun getMangaFeed(
        id: String,
        limit: Int,
        offset: Int,
        authenticated: Boolean
    ): Result<VolumeData> =
        success(
            VolumeData(
                volumes = StubData.Data.CHAPTER_LIST.toChapterList().toVolumeMap(),
                totalChapters = 5,
            )
        )

    override suspend fun getMangaCovers(id: String): Result<Map<String, String>> =
        success(emptyMap())

    override suspend fun getChapter(chapterId: String): Result<Chapter> =
        success(StubData.Responses.GET_CHAPTER.data.toChapter())

    override suspend fun getChapterPages(
        chapterId: String,
        dataSaver: Boolean
    ): Result<List<String>> =
        success(StubData.Responses.GET_CHAPTER_PAGES.convertDataToUrl(dataSaver))

    override suspend fun getChapterList(
        key: String,
        ids: List<String>,
        limit: Int,
        offset: Int
    ): Result<ChapterList> =
        success(StubData.Data.CHAPTER_LIST.toChapterList())

    override suspend fun getChapterListFollows(
        limit: Int,
        offset: Int
    ): Result<UpdatedChapterList> =
        success(
            UpdatedChapterList(
                total = 99,
                data = StubData.Data.FEED_MAP,
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

    override suspend fun getTags(): Result<TagList> =
        success(StubData.Data.TAGS)

    override suspend fun getAuthorList(name: String, limit: Int): Result<List<Author>> =
        success(emptyList())

}