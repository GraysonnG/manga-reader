package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.stores.ChapterFeedDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

const val UPDATES_PAGE_SIZE = 30

class UpdatesScreenViewModel(
    val chapterFeed: ChapterFeedDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UpdatesScreenState())
    val uiState = _uiState.asStateFlow()

    fun initViewModel() {
        chapterFeed.getWithOffset(limit = UPDATES_PAGE_SIZE, offset = 0, loading = true)
        viewModelScope.launch {
            chapterFeed.state.collect {
                _uiState.value = _uiState.value.copy(
                    maxPage = ceil((it.total / UPDATES_PAGE_SIZE) - 1.0).toInt()
                )
            }
        }
    }

    fun loadNextPage() {
        val maxPage = ceil((chapterFeed.state.value.total / UPDATES_PAGE_SIZE) - 1.0)
        val nextPage = min(maxPage.toInt(), _uiState.value.page + 1)
        _uiState.value = _uiState.value.copy(page = nextPage)
        chapterFeed.getWithOffset(
            limit = UPDATES_PAGE_SIZE,
            offset = nextPage * UPDATES_PAGE_SIZE,
            loading = true
        )
    }

    fun loadPreviousPage() {
        val previousPage = max(0, _uiState.value.page - 1)
        _uiState.value = _uiState.value.copy(page = previousPage)
        chapterFeed.getWithOffset(
            limit = UPDATES_PAGE_SIZE,
            offset = previousPage * UPDATES_PAGE_SIZE,
            loading = true
        )
    }

    data class UpdatesScreenState(
        val page: Int = 0,
        val maxPage: Int = 0,
    )
}