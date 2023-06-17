package com.blanktheevil.mangareader.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMangaAggregateResponse(
    val result: String,
    val volumes: Map<String, AggregateVolumeDto>,
)

@JsonClass(generateAdapter = true)
data class AggregateVolumeDto(
    val volume: String?,
    val chapters: Map<String, AggregateChapterDto>,
)

@JsonClass(generateAdapter = true)
data class AggregateChapterDto(
    val chapter: String?,
    val id: String,
)

fun GetMangaAggregateResponse.getChapters(): Map<String, AggregateChapterDto> {
    return volumes.values.flatMap { it.chapters.entries }.associate {
        Pair(it.key, it.value)
    }
}