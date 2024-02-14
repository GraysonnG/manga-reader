package com.blanktheevil.mangareader.data.dto.utils

import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.ScanlationGroup
import com.blanktheevil.mangareader.data.Tag
import com.blanktheevil.mangareader.data.Volume
import com.blanktheevil.mangareader.data.VolumeChapter
import com.blanktheevil.mangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.objects.UserListDto
import com.blanktheevil.mangareader.data.dto.responses.GetChapterPagesResponse
import com.blanktheevil.mangareader.data.dto.responses.GetMangaAggregateResponse
import com.blanktheevil.mangareader.data.dto.responses.GetTagsResponse
import com.blanktheevil.mangareader.data.dto.responses.GetUserListsResponse
import com.blanktheevil.mangareader.data.toTag
import com.squareup.moshi.JsonClass

fun ScanlationGroupDto.toScanlationGroup(): ScanlationGroup = ScanlationGroup(
    id = this.id,
    name = this.attributes.name,
    website = this.attributes.website,
)

fun GetMangaAggregateResponse.toVolumes(): Volumes {
    return this.volumes.entries.mapIndexed { index, entry ->
        val nextChapterList = volumes.entries.toList()
            .getOrNull(index - 1)?.value?.chapters?.values?.toList()
        val prevChapterList = volumes.entries.toList()
            .getOrNull(index + 1)?.value?.chapters?.values?.toList()
        val chapterList = entry.value.chapters.values.toList()

        Volume(
            number = index,
            name = "Volume ${entry.value.volume ?: "None"}",
            chapters = chapterList.mapIndexed { cIndex, it ->
                VolumeChapter(
                    id = it.id,
                    chapter = it.chapter ?: "",
                    next = if (cIndex == 0)
                        nextChapterList?.lastOrNull()?.id
                    else
                        chapterList.getOrNull(cIndex - 1)?.id,
                    prev = if (cIndex == chapterList.lastIndex)
                        prevChapterList?.firstOrNull()?.id
                    else
                        chapterList.getOrNull(cIndex + 1)?.id,

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

@JsonClass(generateAdapter = true)
data class DataList<T>(
    val items: List<T>,
    val offset: Int = 0,
    val limit: Int = 0,
    val total: Int = -1,
)

fun GetTagsResponse.toTagList(): TagList =
    this.data.map { it.toTag() }

fun GetUserListsResponse.parseData(): Map<UserListDto, List<String>> {
    return data.associateWith {
        it.relationships
            ?.filter { rel -> rel.type == "manga" }
            ?.map { rel -> rel.id } ?: emptyList()
    }
}

typealias MangaList = List<Manga>
typealias ChapterList = List<Chapter>
typealias Volumes = List<Volume>
typealias TagList = List<Tag>