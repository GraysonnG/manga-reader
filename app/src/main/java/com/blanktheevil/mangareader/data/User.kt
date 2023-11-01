package com.blanktheevil.mangareader.data

data class User(
    val id: String,
    val username: String,
    val customLists: MutableList<TitledMangaList> = mutableListOf()
)