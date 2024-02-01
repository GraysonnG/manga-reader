package com.blanktheevil.mangareader

import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.ChapterList

data class VolumeData(
    val volumes: VolumeMap,
    val totalChapters: Int,
)
// VolumeNumber -> Chapters
typealias VolumeMap = MutableMap<String, MutableMap<String, ChapterMap>>
// scanlationGroupId -> Chapter
typealias ChapterMap = MutableMap<String, Chapter>

val ChapterMap.title
    get() = this.values.first().title

fun ChapterList.toVolumeMap(): VolumeMap {
    val volumes: VolumeMap = mutableMapOf()

    this.forEach {
        val volume = it.volume ?: "none"
        val chapter = it.chapter ?: "none"
        val scanlationGroupId = it.relatedScanlationGroupId ?: "none"

        if (volumes[volume] == null) {
            volumes[volume] = mutableMapOf(
                chapter to mutableMapOf(
                    scanlationGroupId to it
                )
            )
        }

        if (volumes[volume]?.get(chapter) == null) {
            volumes[volume]?.set(
                chapter, mutableMapOf(
                    scanlationGroupId to it
                )
            )
        }

        if (volumes[volume]?.get(chapter)?.get(scanlationGroupId) == null) {
            volumes[volume]?.get(chapter)?.set(scanlationGroupId, it)
        }
    }

    return volumes
}
