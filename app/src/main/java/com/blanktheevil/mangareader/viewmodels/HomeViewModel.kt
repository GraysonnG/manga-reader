package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.DebouncedValue
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.ChapterFeedDataStore
import com.blanktheevil.mangareader.domain.FollowedMangaDataStore
import com.blanktheevil.mangareader.domain.PopularFeedDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val searchText: String = "",
    val searchMangaList: List<MangaDto> = emptyList(),
)

class HomeViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    val followedManga = FollowedMangaDataStore(mangaDexRepository, viewModelScope)
    val chapterFeed = ChapterFeedDataStore(mangaDexRepository, viewModelScope)
    val popularFeed = PopularFeedDataStore(mangaDexRepository, viewModelScope)

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
        followedManga.get()
        chapterFeed.get()
        popularFeed.get()
    }

    fun refresh() {
        followedManga.refresh()
        chapterFeed.refresh()
        popularFeed.refresh()
    }

    fun searchManga(text: String) {
        if (text.isNotEmpty()) {
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
    }

    fun logout() {
        mangaDexRepository.logout()
    }
}