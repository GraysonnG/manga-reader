package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.utils.ChapterList
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.history.History
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.history.getChapterIds
import com.blanktheevil.mangareader.data.history.mangaIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class HistoryViewModel(
    private val mangaDexRepository: MangaDexRepository,
    private val historyManager: HistoryManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun initViewModel() {
        _uiState.value = _uiState.value.copy(
            history = historyManager.history
        )
        getMangaAndChapters()
    }

    suspend fun getChapters(mangaId: String): ChapterList {
        return when (
            val result = mangaDexRepository.getChapterList(
                key = "history",
                ids = historyManager.history.getChapterIds(
                    mangaId = mangaId
                )
            )
        ) {
            is Result.Success -> result.data
            is Result.Error -> {
                result.error.printStackTrace()
                emptyList()
            }
        }
    }

    fun removeChapterFromHistory(chapterId: String) {
        val history = historyManager.history
        val chapter = history.items.values.firstOrNull { it.containsKey(chapterId) }

        chapter?.remove(chapterId)

        history.items.entries.removeIf {
            it.value.isEmpty()
        }

        historyManager.history = history

        getMangaAndChapters()
    }

    private fun getMangaAndChapters() {
        val mangaIds = historyManager.history.mangaIds

        if (mangaIds.isNotEmpty()) {
            viewModelScope.launch {
                mangaDexRepository.getMangaList("history", mangaIds = mangaIds)
                    .onSuccess {
                        sortManga(it.items)
                    }
                    .onError {
                        _uiState.value = _uiState.value.copy(
                            error = SimpleUIError(
                                title = "Error fetching manga list for history",
                                throwable = it,
                            )
                        )
                    }
            }
        }
    }

    private fun sortManga(manga: MangaList) {
        val history = historyManager.history
        val filteredManga = manga.filter {
            history.items[it.id]?.isNotEmpty() ?: false
        }
        val sortedManga = filteredManga.sortedByDescending { m ->
            historyManager.history.items[m.id]?.values?.maxOf { it.time }
        }

        _uiState.value = _uiState.value.copy(
            manga = sortedManga.take(PAGE_SIZE),
            totalManga = sortedManga,
            currentPage = 0,
            totalPages = ceil(sortedManga.size.toFloat() / PAGE_SIZE).toInt()
        )
    }

    companion object {
        private const val PAGE_SIZE = 30
    }

    data class State(
        val history: History? = null,
        val manga: MangaList? = null,
        val totalManga: MangaList = emptyList(),
        val currentPage: Int = 0,
        val totalPages: Int = 0,
        val error: UIError? = null,
    )
}