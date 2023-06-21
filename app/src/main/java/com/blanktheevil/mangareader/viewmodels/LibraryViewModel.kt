package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max

private const val PAGE_SIZE = 30

class LibraryViewModel : ViewModel() {
    private val mangaDexRepository = MangaDexRepository()

    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()

    fun initViewModel(context: Context) {
        mangaDexRepository.initSessionManager(context)

        loadPage(0)
    }

    fun loadNextPage() {

    }

    fun loadPreviousPage() {

    }

    fun loadPage(page: Int) {
        viewModelScope.launch {
            val result = mangaDexRepository.getUserFollowsList(
                limit = PAGE_SIZE,
                offset = page * PAGE_SIZE
            )

            when(result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        followedMangaList = result.data.data,
                        followedMangaLoading = false,
                        currentPage = page,
                        maxPages = max(
                            getMaxPages(result.data.total),
                            1
                        )
                    )
                }

                is Result.Error -> {
                    // TODO: Handle error
                }
            }
        }
    }

    private fun getMaxPages(
        total: Int?,
    ): Int {
        return ceil(
            total?.toFloat()?.div(PAGE_SIZE.toFloat()) ?: 0f
        ).toInt()
    }

    data class LibraryState(
        val followedMangaList: List<MangaDto> = emptyList(),
        val followedMangaLoading: Boolean = true,
        val currentPage: Int = 0,
        val maxPages: Int = 0,
    )
}