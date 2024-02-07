package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.utils.manga.TitledMangaList

data class User(
    val id: String,
    val username: String,
    val customLists: MutableList<TitledMangaList> = mutableListOf()
)