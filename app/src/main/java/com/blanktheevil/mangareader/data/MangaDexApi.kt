package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.AuthResponse
import com.blanktheevil.mangareader.data.dto.GetChapterIdsResponse
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.GetChapterResponse
import com.blanktheevil.mangareader.data.dto.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.GetMangaResponse
import com.blanktheevil.mangareader.data.dto.GetUserResponse
import com.blanktheevil.mangareader.data.dto.MarkChapterReadRequest
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class AuthData(
    val username: String,
    val password: String,
)

private val defaultContentRatings = listOf(
    "safe",
    "suggestive",
    "erotica",
)

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
        @Query("contentRating[]") contentRating: List<String> = defaultContentRatings,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
    ): GetMangaListResponse

    @GET("manga")
    suspend fun getMangaSearch(
        @Query("title") title: String,
        @Query("limit") limit: Int = 5,
        @Query("contentRating[]") contentRating: List<String> = defaultContentRatings,
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("order[relevance]") order: List<String> = listOf("desc"),
    ): GetMangaListResponse

    @GET("user/follows/manga")
    suspend fun getFollowsList(
        @Header("Authorization") authorization: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art")
    ): GetMangaListResponse

    @GET("user/follows/manga/feed")
    suspend fun getFollowsChapterFeed(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 16,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") translatedLanguage: List<String> = listOf("en"),
        @Query("order[readableAt]") order: List<String> = listOf("desc")
    ): GetChapterListResponse

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
    ): GetChapterResponse

    @GET("manga/{id}/aggregate")
    suspend fun getMangaVolumesAndChapters(
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

    @GET("user/{id}")
    suspend fun getUserInfo(
        @Path("id") id: String,
    ): GetUserResponse
}