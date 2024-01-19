package com.blanktheevil.mangareader.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Manga(
    val id: String,
    val coverArt: String?,
    val title: String,
    val description: String,
    val tags: List<String>,
)
