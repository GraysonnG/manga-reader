package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.DebouncedValue
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val followedMangaList: List<MangaDto> = emptyList(),
    val followedMangaLoading: Boolean = true,
    val chapterFeedManga: List<MangaDto> = emptyList(),
    val chapterFeedChapters: List<ChapterDto> = emptyList(),
    val chapterFeedLoading: Boolean = true,
    val readChapterIds: List<String> = emptyList(),
    val searchText: String = "",
    val searchMangaList: List<MangaDto> = emptyList(),
)

class HomeViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val _textInput = DebouncedValue(
        "",
        300,
        viewModelScope
    )

    val textInput = _textInput.asStateFlow()

    fun onTextChanged(newText: String) {
        _textInput.value = newText
        _uiState.value = _uiState.value.copy(searchText = newText)
        if (newText.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchMangaList = emptyList()
            )
        }
    }

    fun initViewModel(context: Context) {
        mangaDexRepository.initSessionManager(context)
        getFollowedManga()
        getChapterFeed()
    }

    fun searchManga(text: String) {
        viewModelScope.launch {
            when (val result = mangaDexRepository.getMangaSearch(text)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        searchMangaList = result.data
                    )
                }

                is Result.Error -> {
                    // TODO: handle error
                }
            }
        }
    }

    fun logout() {
        mangaDexRepository.logout()
    }

    private fun getFollowedManga() {
        if (_uiState.value.followedMangaList.isEmpty()) {
            viewModelScope.launch {
                val result = mangaDexRepository.getUserFollowsList()
                when (result) {
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        followedMangaList = result.data,
                        followedMangaLoading = false,
                    )
                    is Result.Error -> {}
                }
            }
        }
    }

    private fun getChapterFeed() {
        viewModelScope.launch {
            getFollowsChapterList { mangaIds, chapters ->
                getMangaList(mangaIds) { manga ->
                    getReadMarkers(mangaIds) { readChapters ->
                        _uiState.value = _uiState.value.copy(
                            chapterFeedChapters = chapters,
                            chapterFeedManga = manga,
                            chapterFeedLoading = false,
                            readChapterIds = readChapters
                        )
                    }
                }
            }
        }
    }

    private suspend fun getFollowsChapterList(
        onSuccess: suspend (mangaIds: List<String>, result: List<ChapterDto>) -> Unit
    ) {
        when (val result = mangaDexRepository.getUserFollowsChapterList()) {
            is Result.Success -> {
                val ids = result.data.mapNotNull { chapter ->
                    chapter.relationships.firstOrNull { it.type == "manga" }
                }.mapNotNull { it.id }
                onSuccess(ids, result.data)
            }

            is Result.Error -> {
                // TODO: handle error case
            }
        }
    }

    private suspend fun getMangaList(
        mangaIds: List<String>,
        onSuccess: suspend (
            manga: List<MangaDto>,
        ) -> Unit
    ) {
        when (val mangaListResult = mangaDexRepository
            .getMangaList(ids = mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    mangaListResult.data,
                )
            }
            is Result.Error -> {
                // TODO: handle error state
            }
        }
    }

    private suspend fun getReadMarkers(
        mangaIds: List<String>,
        onSuccess: suspend (
            readChapters: List<String>,
        ) -> Unit
    ) {
        when (val readChaptersResult = mangaDexRepository
            .getReadChapterIdsByMangaIds(mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    readChaptersResult.data
                )
            }

            is Result.Error -> {
                // TODO: handle error state
            }
        }
    }
}