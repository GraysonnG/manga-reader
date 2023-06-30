package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.domain.ChapterFeedDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val PAGE_SIZE = 20

class UpdatesScreenViewModel: ViewModel() {
    private val mangaDexRepository: MangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(UpdatesScreenState())
    val uiState = _uiState.asStateFlow()
    val chapterFeed = ChapterFeedDataStore(mangaDexRepository, viewModelScope)


    fun initViewModel(context: Context) {
        mangaDexRepository.initRepositoryManagers(context = context)
        chapterFeed.getWithOffset(limit = PAGE_SIZE, offset = 0, loading = true)
        viewModelScope.launch {
            chapterFeed.state.collect {
                _uiState.value = _uiState.value.copy(
                    maxPage = ceil((it.total / PAGE_SIZE) - 1.0).toInt()
                )
            }
        }
    }

    fun loadNextPage() {
        val maxPage = ceil((chapterFeed.state.value.total / PAGE_SIZE) - 1.0)
        val nextPage = min(maxPage.toInt(), _uiState.value.page + 1)
        _uiState.value = _uiState.value.copy(page = nextPage)
        chapterFeed.getWithOffset(limit = PAGE_SIZE, offset = nextPage * PAGE_SIZE, loading = true)
    }

    fun loadPreviousPage() {
        val previousPage = max(0, _uiState.value.page - 1)
        _uiState.value = _uiState.value.copy(page = previousPage)
        chapterFeed.getWithOffset(limit = PAGE_SIZE, offset = previousPage * PAGE_SIZE, loading = true)
    }

    data class UpdatesScreenState(
        val page: Int = 0,
        val maxPage: Int = 0,
    )
}