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
            manga = sortedManga
        )
    }

    data class State(
        val history: History? = null,
        val manga: List<MangaDto>? = null,

    )
}