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

val MangaDto.description: String
    get() {
        return this.attributes.description?.get("en") ?:
        this.attributes.description?.values?.firstOrNull() ?:
        "Could not find description for this manga."
    }