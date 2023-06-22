package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class GetMangaListResponse(
    val result: String?,
    val response: String?,
    val data: List<MangaDto>,
    val limit: Int?,
    val offset: Int?,
    val total: Int?,
)

@JsonClass(generateAdapter = true)
data class MangaDto(
    val id: String,
    val type: String,
    val attributes: MangaAttributesDto,
    val relationships: List<RelationshipDto>,
)

@JsonClass(generateAdapter = true)
data class MangaAttributesDto(
    val title: Map<String, String>,
    val description: Map<String, String>,
    val isLocked: Boolean?,
    val links: Map<String, String>?,
    val originalLanguage: String?,
    val lastVolume: String?,
    val lastChapter: String?,
    val publicationDemographic: String?,
    val status: String?, // convert to enum
    val year: Int?,
    val tags: List<TagsDto>?,
    val state: String?, // convert to enum
    val createdAt: Date?,
    val updatedAt: Date?,
    val latestUploadedChapter: String?,
)

@JsonClass(generateAdapter = true)
data class TagsDto(
    val id: String?,
    val type: String?,
    val attributes: TagsAttributesDto,
    val relationships: List<RelationshipDto>
)

@JsonClass(generateAdapter = true)
data class TagsAttributesDto(
    val name: Map<String, String>,
    val description: Map<String, String>,
    val group: String?,
    val version: Int?,
)

@JsonClass(generateAdapter = true)
data class RelationshipDto(
    val id: String?,
    val type: String?,
    val attributes: RelationshipAttributesDto?,
    val relationships: List<RelationshipDto>?,
)

@JsonClass(generateAdapter = true)
data class RelationshipAttributesDto(
    val name: Map<String, String>?,
    val fileName: String?,
    val description: Any?,
    val group: String?,
)