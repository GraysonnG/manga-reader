package com.blanktheevil.mangareader.ui.reader_v2

import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.VolumeChapter
import com.blanktheevil.mangareader.viewmodels.ReaderType
import kotlinx.coroutines.flow.StateFlow

interface ReaderManager {
    val state: StateFlow<ReaderManagerState>

    fun setChapter(chapter: String)
    fun closeReader()
    fun expandReader()
    fun shrinkReader()
    fun toggleReader()
    fun nextPage()
    fun prevPage()
    fun nextChapter()
    fun prevChapter()
    fun markChapterRead()
}

data class ReaderManagerState(
    val manga: Manga? = null,
    val mangaId: String? = null,
    val chapters: List<VolumeChapter> = emptyList(),
    val currentChapter: Chapter? = null,
    val currentChapterId: String? = null,
    val currentVolumeChapter: VolumeChapter? = null,
    val currentChapterLoading: Boolean = true,
    val currentChapterPageUrls: List<String> = emptyList(),
    val currentChapterPageLoaded: MutableList<Boolean> = ArrayList(),
    val currentPage: Int = 0,
    val expanded: Boolean = true,
    val readerType: ReaderType = ReaderType.PAGE,
)