package com.blanktheevil.mangareader.viewmodels

import android.content.Context
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
    val chapterReadIds: List<String> = emptyList(),
    val mangaIsFollowed: Boolean = false,
    val loading: Boolean = true,
)

class MangaDetailViewModel : ViewModel() {
    private val mangaDexRepository: MangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(MangaDetailState())
    val uiState = _uiState.asStateFlow()

    private val langFilter = "en"

    fun getMangaDetails(id: String, context: Context) {
        viewModelScope.launch {
            mangaDexRepository.initRepositoryManagers(context)
            when (val mangaDetails = mangaDexRepository.getMangaDetails(id)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    data = mangaDetails.data,
                )
                is Result.Error -> _uiState.value = _uiState.value.copy()
            }

            when (mangaDexRepository.getIsUserFollowingManga(id)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    mangaIsFollowed = true,
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    mangaIsFollowed = false,
                )
            }

            when (val mangaChapters = mangaDexRepository.getMangaChapters(id)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    chapters = sortChapters(
                        mangaChapters.data
                            .filter { it.attributes.translatedLanguage == langFilter }
                    )
                )
                is Result.Error -> _uiState.value = _uiState.value.copy()
            }

            when (val mangaChaptersRead = mangaDexRepository.getReadChapterIdsByMangaIds(
                listOf(id)
            )) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    chapterReadIds = mangaChaptersRead.data
                )
                is Result.Error -> {}
            }

            _uiState.value = _uiState.value.copy(loading = false)
        }
    }

    fun followManga() {
        _uiState.value.data?.let {
            viewModelScope.launch {
                when (mangaDexRepository.setMangaFollowed(it.id)) {
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        mangaIsFollowed = true,
                    )
                    is Result.Error -> {}
                }
            }
        }
    }

    fun unFollowManga() {
        _uiState.value.data?.let {
            viewModelScope.launch {
                when (mangaDexRepository.setMangaUnfollowed(it.id)) {
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        mangaIsFollowed = false,
                    )
                    is Result.Error -> {}
                }
            }
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