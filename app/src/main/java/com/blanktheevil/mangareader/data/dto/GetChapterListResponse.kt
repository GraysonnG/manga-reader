package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

@JsonClass(generateAdapter = true)
data class GetChapterListResponse(
    val result: String,
    val response: String,
    val data: List<ChapterDto>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)

@JsonClass(generateAdapter = true)
data class ChapterDto(
    val id: String,
    val type: String,
    val attributes: ChapterAttributesDto,
    val relationships: List<JSONObject>,
)

@JsonClass(generateAdapter = true)
data class ChapterAttributesDto(
    val volume: String?,
    val chapter: String?,
    val title: String?,
    val translatedLanguage: String?,
    val externalUrl: String?,
    val publishAt: Date?,
    val readableAt: Date?,
    val createdAt: Date?,
    val updatedAt: Date?,
    val pages: Int?,
    val version: Int?,
)

@JsonClass(generateAdapter = true)
data class ChapterScanlationGroupDto(
    val id: String,
    val type: String,
    val attributes: ChapterScanlationGroupAttributesDto?,
)

@JsonClass(generateAdapter = true)
data class ChapterScanlationGroupAttributesDto(
    val name: String,
    val altNames: List<Any>?,
    val website: String?,

    )

fun ChapterDto.getMangaRelationship(moshi: Moshi): MangaDto? =
    relationships.firstOrNull { it.getString("type") == "manga" }?.let {
        if (!it.has("attributes")) {
            it.put("attributes", JSONObject().apply {
                this@apply.put("title", JSONObject().apply { put("en", "Unknown") })
            })
        }
        if (!it.has("relationships")) {
            it.put("relationships", JSONArray())
        }
        moshi.adapter(MangaDto::class.java).fromJson(it.toString())
    }

fun ChapterDto.getScanlationGroupRelationship(moshi: Moshi): ChapterScanlationGroupDto? =
    relationships.firstOrNull { it.getString("type") == "scanlation_group" }?.let {
        moshi.adapter(ChapterScanlationGroupDto::class.java).fromJson(it.toString())
    }
