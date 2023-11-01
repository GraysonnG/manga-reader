package com.blanktheevil.mangareader.data

import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.GetMangaListResponse
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.helpers.description
import com.blanktheevil.mangareader.helpers.getCoverImageUrl
import com.blanktheevil.mangareader.helpers.shortTitle
import com.blanktheevil.mangareader.helpers.tags
import com.blanktheevil.mangareader.helpers.title
import com.squareup.moshi.Moshi
import org.json.JSONArray
import org.json.JSONObject

fun MangaDto.toManga(): Manga = Manga(
    id = this.id,
    coverArt = this.getCoverImageUrl(),
    title = this.title,
    description = this.description,
    tags = this.tags
)

fun ChapterDto.toChapter(
    moshi: Moshi? = null,
    isRead: Boolean = false,
): Chapter {
    val relatedMangaRel = this.relationships.firstOrNull { it.getString("type") == "manga" }
    val relatedMangaId = relatedMangaRel?.getString("id")
    val relatedManga = relatedMangaRel?.let {
        if (!it.has("attributes")) {
            it.put("attributes", JSONObject().apply {
                this@apply.put("title", JSONObject().apply { put("en", "Unknown") })
            })
        }
        if (!it.has("relationships")) {
            it.put("relationships", JSONArray())
        }

        try {
            moshi?.adapter(MangaDto::class.java)?.fromJson(it.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val relatedScanGroupRel = this.relationships.firstOrNull {
        it.getString("type") == "scanlation_group"
    }
    val relatedScanGroupId = relatedScanGroupRel?.getString("id")
    val relatedScanGroup = relatedScanGroupRel?.let {
        try {
            moshi?.adapter(ChapterScanlationGroupDto::class.java)?.fromJson(it.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    return Chapter(
        id = this.id,
        title = this.title,
        volume = this.attributes.volume,
        chapter = this.attributes.chapter,
        shortTitle = this.shortTitle,
        externalUrl = this.attributes.externalUrl,
        relatedMangaId = relatedMangaId,
        relatedManga = relatedManga?.toManga(),
        relatedScanlationGroupId = relatedScanGroupId,
        relatedScanlationGroup = relatedScanGroup?.toScanlationGroup(),
        isRead = isRead,
    )
}

fun ChapterScanlationGroupDto.toScanlationGroup(): ScanlationGroup = ScanlationGroup(
    id = this.id,
    name = this.attributes?.name ?: "Unknown",
    website = this.attributes?.website,
)

fun List<MangaDto>.toMangaList(): MangaList =
    map { it.toManga() }

fun List<ChapterDto>.toChapterList(
    moshi: Moshi? = null,
    readIds: List<String>? = null,
    allRead: Boolean = false,
): ChapterList = map {
    it.toChapter(
        moshi = moshi,
        isRead = allRead || readIds?.contains(it.id) == true
    )
}

fun ChapterList.toDataList(
    limit: Int = 0,
    offset: Int = 0,
    total: Int = 0,
): DataList<Chapter> = DataList(
    items = this,
    limit = limit,
    offset = offset,
    total = total,
)

fun GetMangaAggregateResponse.toVolumes(): Volumes {
    return this.volumes.entries.mapIndexed { index, entry ->
        val chapterList = entry.value.chapters.values.toList()

        Volume(
            number = index,
            name = "Volume ${entry.value.volume ?: "None"}",
            chapters = chapterList.mapIndexed { cIndex, it ->
                VolumeChapter(
                    id = it.id,
                    next = chapterList.getOrNull(cIndex - 1)?.id,
                    prev = chapterList.getOrNull(cIndex + 1)?.id,
                )
            }
        )
    }
}

fun GetChapterPagesResponse.convertDataToUrl(
    dataSaver: Boolean,
): List<String> {
    val imageQuality = if (dataSaver && chapter.dataSaver != null) "data-saver" else "data"
    return (if (dataSaver && chapter.dataSaver != null) chapter.dataSaver else chapter.data)?.map {
        "$baseUrl/$imageQuality/${chapter.hash}/$it"
    } ?: emptyList()
}

fun GetMangaListResponse.toDataList(): DataList<Manga> = DataList(
    items = this.data.toMangaList(),
    offset = this.offset ?: 0,
    limit = this.limit ?: 0,
    total = this.total ?: -1,
)

data class TitledMangaList(
    val title: String,
    val mangaList: MangaList,
    val visibility: String? = null,
    val version: Int? = null,
)

data class DataList<T>(
    val items: List<T>,
    val offset: Int = 0,
    val limit: Int = 0,
    val total: Int = -1,
)


typealias MangaList = List<Manga>
typealias ChapterList = List<Chapter>
typealias Volumes = List<Volume>