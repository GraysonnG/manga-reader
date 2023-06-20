package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarkChapterReadRequest(
    val chapterIdsRead: List<String>,
    val chapterIdsUnread: List<String>,
)
