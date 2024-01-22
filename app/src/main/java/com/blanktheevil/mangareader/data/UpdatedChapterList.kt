package com.blanktheevil.mangareader.data

import com.squareup.moshi.JsonClass

typealias ChapterFeedItems = Map<Manga, ChapterList>

@JsonClass(generateAdapter = true)
data class UpdatedChapterList(
    val total: Int,
    val data: Map<Manga, List<Chapter>>
)