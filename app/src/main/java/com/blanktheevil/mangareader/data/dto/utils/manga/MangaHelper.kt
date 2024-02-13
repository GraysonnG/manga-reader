package com.blanktheevil.mangareader.data.dto.utils.manga

import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.dto.objects.CoverArtDto
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.blanktheevil.mangareader.data.dto.responses.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.utils.DataList
import com.blanktheevil.mangareader.data.dto.utils.MangaList

fun MangaDto.getCoverImageUrl(): String? {
    val fileName = relationships
        ?.getFirstByType(CoverArtDto::class.java)
        ?.attributes
        ?.fileName ?: return null

    return "https://uploads.mangadex.org/covers/${this.id}/$fileName.256.jpg"
}

val MangaDto.title: String
    get() {
        return this.attributes.title["en"] ?: this.attributes.title.values.firstOrNull()
        ?: "Could not find title."
    }

val MangaDto.description: String
    get() {
        return this.attributes.description?.get("en")
            ?: this.attributes.description?.values?.firstOrNull()
            ?: "Could not find description for this manga."
    }

val MangaDto.tags: List<String>
    get() {
        return this.attributes.tags?.mapNotNull {
            it.attributes.name["en"] ?: it.attributes.name.values.firstOrNull()
        } ?: emptyList()
    }

fun MangaDto.toManga(): Manga = Manga(
    id = this.id,
    coverArt = this.getCoverImageUrl(),
    title = this.title,
    description = this.description,
    tags = this.tags
)

fun List<MangaDto>.toMangaList(): MangaList =
    map { it.toManga() }

fun GetMangaListResponse.toDataList(): DataList<Manga> = DataList(
    items = this.data.toMangaList(),
    offset = this.offset,
    limit = this.limit,
    total = this.total,
)

data class TitledMangaList(
    val title: String,
    val mangaList: MangaList,
    val visibility: String? = null,
    val version: Int? = null,
)
