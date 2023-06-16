package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MangaDetailState(
    val data: MangaDto? = null,
    val chapters: List<ChapterDto> = emptyList(),
    val loading: Boolean = true,
)

class MangaDetailViewModel : ViewModel() {
    val mangaDexRepository: MangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(MangaDetailState())
    val uiState = _uiState.asStateFlow()

    val langFilter = "en"

    fun getMangaDetails(id: String) {
        viewModelScope.launch {
            val mangaDetails = mangaDexRepository.getMangaDetails(id)

            when (mangaDetails) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    data = mangaDetails.data,
                )
                is Result.Error -> _uiState.value = _uiState.value.copy()
            }

            val mangaChapters = mangaDexRepository.getMangaChapters(id)

            when (mangaChapters) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    chapters = sortChapters(
                        mangaChapters.data
                            .filter { it.attributes.translatedLanguage == langFilter }
                    )
                )
                is Result.Error -> _uiState.value = _uiState.value.copy()
            }

            _uiState.value = _uiState.value.copy(loading = false)
        }
    }

    private fun sortChapters(chapters: List<ChapterDto>): List<ChapterDto> {
        return chapters.sortedWith(
            compareByDescending {
                it.attributes.chapter?.toFloatOrNull()
            }
        )
    }
}