package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaCoversResponse(
    val data: List<CoverDto>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

@JsonClass(generateAdapter = true)
data class CoverDto(
    val id: String,
    val type: String,
    val attributes: CoverAttributesDto
)

@JsonClass(generateAdapter = true)
data class CoverAttributesDto(
    val volume: String,
    val fileName: String,
    val description: String,
    val version: Int
)