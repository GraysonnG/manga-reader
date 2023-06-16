package com.blanktheevil.mangareader.helpers

import com.blanktheevil.mangareader.data.dto.MangaDto

fun MangaDto.getCoverImageUrl(): String? {
    val fileName = relationships.firstOrNull {
        it.type == "cover_art"
    }?.attributes?.fileName
    return "https://uploads.mangadex.org/covers/${this.id}/$fileName.256.jpg"
}

val MangaDto.title: String
    get() {
        return this.attributes.title["en"] ?:
        this.attributes.title.values.firstOrNull() ?:
        "Could not find title."
    }