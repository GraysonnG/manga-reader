package com.blanktheevil.mangareader.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chapter(
    val id: String,
    val title: String,
    val volume: String?,
    val chapter: String?,
    val shortTitle: String,
    val externalUrl: String?,
    val relatedMangaId: String?,
    val relatedManga: Manga?,
    val relatedScanlationGroupId: String?,
    val relatedScanlationGroup: ScanlationGroup?,
    val isRead: Boolean,
)
