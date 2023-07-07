package com.blanktheevil.mangareader.helpers

import com.blanktheevil.mangareader.data.dto.ChapterDto

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

        return listOfNotNull(
            listOfNotNull(
                chapterNumber?.let { "Ch. $it" }
            ).joinToString(separator = " "),
            title?.let { it.ifEmpty { null } }
        ).joinToString(separator = " - ")
    }