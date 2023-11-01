package com.blanktheevil.mangareader.data

data class Manga(
    val id: String,
    val coverArt: String?,
    val title: String,
    val description: String,
    val tags: List<String>,
)
