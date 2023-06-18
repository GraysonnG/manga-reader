package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class GetChapterListResponse(
    val result: String,
    val response: String,
    val data: List<ChapterDto>,
)

@JsonClass(generateAdapter = true)
data class ChapterDto(
    val id: String,
    val type: String,
    val attributes: ChapterAttributesDto,
    val relationships: List<RelationshipDto>,
)

@JsonClass(generateAdapter = true)
data class ChapterAttributesDto(
    val volume: String?,
    val chapter: String?,
    val title: String?,
    val translatedLanguage: String?,
    val externalUrl: Any?,
    val publishAt: Date?,
    val readableAt: Date?,
    val createdAt: Date?,
    val updatedAt: Date?,
    val pages: Int?,
    val version: Int?,
)
