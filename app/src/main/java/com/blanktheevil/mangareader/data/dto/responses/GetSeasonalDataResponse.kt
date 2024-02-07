package com.blanktheevil.mangareader.data.dto.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSeasonalDataResponse(
    val id: String,
    val name: String?,
    @Json(name = "manga_ids")
    val mangaIds: List<String>,
)
