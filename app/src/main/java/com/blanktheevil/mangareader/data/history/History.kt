package com.blanktheevil.mangareader.data.history

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class History(
    val items: MutableMap<String, MutableMap<String, Date>> = mutableMapOf(),
)

val History.mangaIds: List<String>
    get() = items.keys.toList()

fun History.getChapterIds(mangaId: String): List<String> {
    return items[mangaId]?.keys?.toList() ?: emptyList()
}