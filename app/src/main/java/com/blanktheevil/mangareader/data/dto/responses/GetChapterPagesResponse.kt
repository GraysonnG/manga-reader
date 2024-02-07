package com.blanktheevil.mangareader.data.dto.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChapterPagesResponse(
    val result: String,
    val baseUrl: String,
    val chapter: ChapterPagesDataDto,
) {
    @JsonClass(generateAdapter = true)
    data class ChapterPagesDataDto(
        val hash: String?,
        val data: List<String>?,
        val dataSaver: List<String>?,
    )
}

