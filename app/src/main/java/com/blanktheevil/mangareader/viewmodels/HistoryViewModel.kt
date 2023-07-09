package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.history.History
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.history.getChapterIds
import com.blanktheevil.mangareader.data.history.mangaIds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class HistoryViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val historyManager = HistoryManager.getInstance()
    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun initViewModel(context: Context) {
        historyManager.init(context)
        _uiState.value = _uiState.value.copy(
            history = historyManager.history
        )
        getMangaAndChapters()
    }

    suspend fun getChapters(mangaId: String): List<ChapterDto> {
        return when (
            val result = mangaDexRepository.getChapterList(
                ids = historyManager.history.getChapterIds(
                    mangaId = mangaId
                )
            )
        ) {
            is Result.Success -> {
                result.data
            }

            is Result.Error -> {
                result.error.printStackTrace()
                emptyList()
            }
        }
    }

    fun nextPage() {
        val nextPage = _uiState.value.currentPage + 1
        val startIndex = nextPage * PAGE_SIZE
        val endIndex = startIndex + PAGE_SIZE
        _uiState.value = _uiState.value.copy(
            manga = _uiState.value.totalManga.subList(startIndex, endIndex),
            currentPage = nextPage
        )
    }

    fun previousPage() {
        val prevPage = _uiState.value.currentPage - 1
        val startIndex = prevPage * PAGE_SIZE
        val endIndex = startIndex + PAGE_SIZE
        _uiState.value = _uiState.value.copy(
            manga = _uiState.value.totalManga.subList(startIndex, endIndex),
            currentPage = prevPage
        )
    }

    private fun getMangaAndChapters() {
        val mangaIds = historyManager.history.mangaIds

        if (mangaIds.isNotEmpty()) {
            viewModelScope.launch {
                when (val result = mangaDexRepository.getMangaList(ids = mangaIds)) {
                    is Result.Success -> {
                        sortManga(result.data)
                    }

                    is Result.Error -> {
                        // TODO: handle error
                    }
                }
            }
        }
    }

    private fun sortManga(manga: List<MangaDto>) {
        val sortedManga = manga.sortedByDescending { mangaDto ->
            historyManager.history.items[mangaDto.id]?.values?.maxOf { it.time }
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
        val manga: List<MangaDto>? = null,
        val totalManga: List<MangaDto> = emptyList(),
        val currentPage: Int = 0,
        val totalPages: Int = 0,
    )
}