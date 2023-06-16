package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaResponse(
    val result: String?,
    val response: String?,
    val data: MangaDto,
)