package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChapterResponse(
    val result: String,
    val response: String,
    val data: ChapterDto,
)
