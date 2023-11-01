package com.blanktheevil.mangareader.data

typealias ChapterFeedItems = Map<Manga, ChapterList>

data class UpdatedChapterList(
    val total: Int,
    val data: ChapterFeedItems
)