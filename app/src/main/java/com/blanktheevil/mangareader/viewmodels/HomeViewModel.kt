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

data class HomeState(
    val followedMangaList: List<MangaDto> = emptyList(),
    val chapterFeedManga: List<MangaDto> = emptyList(),
    val chapterFeedChapters: List<ChapterDto> = emptyList(),
    val followedMangaLoading: Boolean = true,
    val chapterFeedLoading: Boolean = true,
)

class HomeViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun initViewModel(context: Context) {
        mangaDexRepository.initSessionManager(context)
        getFollowedManga()
        getChapterFeed()
    }

    fun logout() {
        mangaDexRepository.logout()
    }

    fun getFollowedManga() {
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

    fun getChapterFeed() {
        viewModelScope.launch {
            val result1 = mangaDexRepository.getUserFollowsChapterList()
            when (result1) {
                is Result.Success -> {
                    val ids = result1.data.mapNotNull { chapter ->
                        chapter.relationships.firstOrNull { it.type == "manga" }
                    }.mapNotNull { it.id }

                    val result2 = mangaDexRepository.getMangaList(ids = ids)
                    when (result2) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                chapterFeedChapters = result1.data,
                                chapterFeedManga = result2.data,
                                chapterFeedLoading = false,
                            )
                        }
                        is Result.Error -> {}
                    }
                }
                is Result.Error -> {

                }
            }
        }
    }
}