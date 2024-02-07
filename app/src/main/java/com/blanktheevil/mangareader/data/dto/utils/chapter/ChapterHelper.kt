package com.blanktheevil.mangareader.data.dto.utils.chapter

import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.dto.objects.ChapterDto
import com.blanktheevil.mangareader.data.dto.objects.MangaDto
import com.blanktheevil.mangareader.data.dto.objects.ScanlationGroupDto
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import com.blanktheevil.mangareader.data.dto.utils.manga.toManga
import com.blanktheevil.mangareader.data.dto.utils.toScanlationGroup

val ChapterDto.title: String
    get() {
        val chapterNumber = this.attributes.chapter
        val volumeNumber = this.attributes.volume
        val title = this.attributes.title

        return listOfNotNull(
            listOfNotNull(
                volumeNumber?.let { "Vol. $it" },
                chapterNumber?.let { "Ch. $it" }
            ).joinToString(separator = " "),
            title?.let { it.ifEmpty { null } }
        ).joinToString(separator = " - ")
    }

val ChapterDto.shortTitle: String
    get() {
        val chapterNumber = this.attributes.chapter
        val title = this.attributes.title

        if (title.isNullOrEmpty()) return "Ch. $chapterNumber"

        return title
    }

val ChapterDto.mediumTitle: String
    get() {
        val chapterNumber = this.attributes.chapter
        val title = this.attributes.title

        return listOfNotNull(
            chapterNumber?.let { "Ch. $it" },
            title?.ifEmpty { null },
        ).joinToString(separator = " - ")
    }

fun ChapterDto.toChapter(
    isRead: Boolean = false,
): Chapter {
    val relatedManga = this.relationships?.getFirstByType(MangaDto::class.java)
    val relatedMangaId =
        relatedManga?.id ?: this.relationships?.firstOrNull { it.type == "manga" }?.id
    val relatedScanGroup = this.relationships?.getFirstByType(ScanlationGroupDto::class.java)
    val relatedScanGroupId =
        relatedScanGroup?.id
            ?: this.relationships?.firstOrNull { it.type == "scanlation_group" }?.id

    return Chapter(
        id = this.id,
        title = this.title,
        volume = this.attributes.volume,
        chapter = this.attributes.chapter,
        shortTitle = this.shortTitle,
        mediumTitle = this.mediumTitle,
        externalUrl = this.attributes.externalUrl,
        relatedMangaId = relatedMangaId,
        relatedManga = relatedManga?.toManga(),
        relatedScanlationGroupId = relatedScanGroupId,
        relatedScanlationGroup = relatedScanGroup?.toScanlationGroup(),
        isRead = isRead,
    )
}

fun List<ChapterDto>.toChapterList(
    readIds: List<String>? = null,
    allRead: Boolean = false,
): ChapterList = map {
    it.toChapter(
        isRead = allRead || readIds?.contains(it.id) == true
    )
}