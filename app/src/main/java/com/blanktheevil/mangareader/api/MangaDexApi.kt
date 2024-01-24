package com.blanktheevil.mangareader.api

import com.blanktheevil.mangareader.data.TagsMode
import com.blanktheevil.mangareader.data.dto.AuthData
import com.blanktheevil.mangareader.data.dto.AuthResponse
import com.blanktheevil.mangareader.data.dto.GetChapterIdsResponse
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.GetChapterResponse
import com.blanktheevil.mangareader.data.dto.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.GetMangaResponse
import com.blanktheevil.mangareader.data.dto.GetTagsResponse
import com.blanktheevil.mangareader.data.dto.GetUserListsResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.blanktheevil.mangareader.data.session.Refresh
import com.blanktheevil.mangareader.data.settings.ContentRatings
import com.blanktheevil.mangareader.data.settings.defaultContentRatings
import com.blanktheevil.mangareader.helpers.getCreatedAtSinceString
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MangaDexApi {
    @POST("auth/login")
    suspend fun authLogin(@Body authData: AuthData): AuthResponse

    @POST("auth/refresh")
    suspend fun authRefresh(@Body refreshToken: Refresh): AuthResponse

    @POST("manga/{id}/read")
    suspend fun markChapterRead(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body body: MarkChapterReadRequest,
    )

    @GET("manga/{id}")
    suspend fun getMangaById(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
    ): GetMangaResponse

    @GET("manga")
    suspend fun getManga(
        @Query("ids[]") ids: List<String>,
        @Query("limit") limit: Int = 32,
        @Query("offset") offset: Int = 0,
        @Query("contentRating[]") contentRating: ContentRatings = defaultContentRatings,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaSearch(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0,
        @Query("contentRating[]") contentRating: ContentRatings = defaultContentRatings,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[relevance]") order: List<String> = listOf("desc"),
        @Query("title") title: String? = null,
        @Query("publicationDemographic[]") publicationDemographic: List<String>? = null,
        @Query("status[]") status: List<String>? = null,
        @Query("includedTags[]") includedTags: List<String>? = null,
        @Query("excludedTags[]") excludedTags: List<String>? = null,
        @Query("includedTagsMode[]") includedTagsMode: TagsMode? = null,
        @Query("excludedTagsMode[]") excludedTagsMode: TagsMode? = null,
        @Query("authors[]") authors: List<String>? = null,
        @Query("artists[]") artists: List<String>? = null,
        @Query("year") year: String? = null,
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaPopular(
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[followedCount]") order: List<String> = listOf("desc"),
        @Query("contentRating[]") contentRating: ContentRatings = defaultContentRatings,
        @Query("hasAvailableChapters") hasAvailableChapters: Boolean = true,
        @Query("createdAtSince") createdAtSince: String = getCreatedAtSinceString(),
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaRecent(
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[latestUploadedChapter]") order: List<String> = listOf("desc"),
        @Query("contentRating[]") contentRating: ContentRatings = defaultContentRatings,
        @Query("hasAvailableChapters") hasAvailableChapters: Boolean = true,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GetMangaListResponse

    @GET("user/follows/manga")
    suspend fun getFollowsList(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art")
    ): GetMangaListResponse

    @GET("user/follows/manga/feed")
    suspend fun getFollowsChapterFeed(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 15,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("order[readableAt]") order: List<String> = listOf("desc"),
        @Query("includes[]") includes: List<String> = listOf("scanlation_group"),
    ): GetChapterListResponse

    @GET("user/follows/manga/{id}")
    suspend fun getIsUserFollowingManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @Deprecated("Use Aggregate Instead")
    @GET("manga/{id}/feed")
    suspend fun getMangaChapters(
        @Path("id") id: String,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("order[chapter]") order: List<String> = listOf("desc")
    ): GetChapterListResponse

    @GET("chapter/{id}")
    suspend fun getChapter(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("scanlation_group", "manga"),
    ): GetChapterResponse

    @GET("chapter")
    suspend fun getChapterList(
        @Query("ids[]") ids: List<String>,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("order[chapter]") order: List<String> = listOf("desc"),
        @Query("includes[]") includes: List<String> = listOf("scanlation_group"),
    ): GetChapterListResponse

    @GET("manga/{id}/aggregate")
    suspend fun getMangaAggregate(
        @Path("id") id: String,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
    ): GetMangaAggregateResponse

    @GET("at-home/server/{chapterId}")
    suspend fun getChapterPages(
        @Path("chapterId") chapterId: String,
    ): GetChapterPagesResponse

    @GET("manga/read")
    suspend fun getReadChapterIdsByMangaIds(
        @Header("Authorization") authorization: String,
        @Query("ids[]") ids: List<String>,
    ): GetChapterIdsResponse

    @GET("manga/tag")
    suspend fun getAllTags(): GetTagsResponse

    @GET("user/{id}")
    suspend fun getUserInfo(
        @Path("id") id: String,
    ): GetUserResponse


    @POST("manga/{id}/follow")
    suspend fun followManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @DELETE("manga/{id}/follow")
    suspend fun unfollowManga(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
    ): Any

    @GET("user/list")
    suspend fun getUserLists(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 20,
    ): GetUserListsResponse

    @POST("manga/{id}/list/{listId}")
    suspend fun addMangaToList(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Path("listId") listId: String,
    )

    @DELETE("manga/{id}/list/{listId}")
    suspend fun removeMangaFromList(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Path("listId") listId: String,
    )
}