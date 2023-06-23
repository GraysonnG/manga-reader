package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.MangaList
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.ui.screens.LibraryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val PAGE_SIZE = 30

class LibraryViewModel : ViewModel() {
    private val mangaDexRepository = MangaDexRepository()

    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()
    var libraryType: LibraryType? = null

    fun initViewModel(context: Context, libraryType: LibraryType) {
        this.libraryType = libraryType
        mangaDexRepository.initSessionManager(context)

        loadPage(0)
    }

    fun loadNextPage() {
        loadPage(_uiState.value.currentPage + 1, true)
    }

    private fun loadPage(page: Int, addItems: Boolean = false) {
        val limit = _uiState.value.limit
        if (limit >= 0 && page * PAGE_SIZE > limit) return

        _uiState.value = _uiState.value.copy(followedMangaLoading = true)

        viewModelScope.launch {
            val result = when (libraryType) {
                LibraryType.FOLLOWS -> mangaDexRepository.getUserFollowsList(
                    limit = PAGE_SIZE,
                    offset = page * PAGE_SIZE
                )

                LibraryType.POPULAR -> mangaDexRepository.getPopularMangaList(
                    limit = PAGE_SIZE,
                    offset = page * PAGE_SIZE
                )
                else -> Result.Error(Exception("Invalid library type"))
            }

            when(result) {
                is Result.Success -> {
                    val followedMangaList = if (addItems) {
                        _uiState.value.followedMangaList + result.data.data
                    } else {
                        result.data.data
                    }

                    _uiState.value = _uiState.value.copy(
                        followedMangaList = followedMangaList,
                        followedMangaLoading = false,
                        currentPage = page,
                        maxPages = max(
                            getMaxPages(result.data.total),
                            1
                        ),
                        limit = min(result.data.total ?: -1, 200),
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
        val followedMangaList: MangaList = emptyList(),
        val followedMangaLoading: Boolean = true,
        val currentPage: Int = 0,
        val maxPages: Int = 0,
        val limit: Int = -1,
    )
}