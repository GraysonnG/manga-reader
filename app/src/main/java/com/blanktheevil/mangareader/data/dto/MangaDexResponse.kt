package com.blanktheevil.mangareader.data.dto

interface MangaDexResponse<T> {
    val data: T
}

interface MangaDexListResponse<T : GenericMangaDexObject> : MangaDexResponse<List<T>> {
    override val data: List<T>
    val limit: Int
    val offset: Int
    val total: Int
}