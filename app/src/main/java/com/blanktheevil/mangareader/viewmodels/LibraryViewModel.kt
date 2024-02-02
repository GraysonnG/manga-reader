package com.blanktheevil.mangareader.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.ui.screens.LibraryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val PAGE_SIZE = 30

class LibraryViewModel(
    private val mangaDexRepository: MangaDexRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryState())
    val uiState = _uiState.asStateFlow()
    private var libraryType: LibraryType? = null

    fun initViewModel(libraryType: LibraryType) {
        this.libraryType = libraryType

        loadPage(0)
    }

    fun loadNextPage() {
        loadPage(_uiState.value.currentPage + 1, true)
    }

    private fun loadPage(page: Int, addItems: Boolean = false) {
        val limit = _uiState.value.limit
        if (limit >= 0 && page * PAGE_SIZE > limit) return

        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            val result = when (libraryType) {
                LibraryType.FOLLOWS -> mangaDexRepository.getMangaFollows(
                    limit = PAGE_SIZE,
                    offset = page * PAGE_SIZE
                )

                LibraryType.POPULAR -> mangaDexRepository.getMangaPopular(
                    limit = PAGE_SIZE,
                    offset = page * PAGE_SIZE
                )

                else -> Result.Error(Exception("Invalid library type"))
            }

            result
                .onSuccess {
                    val followedMangaList = if (addItems) {
                        _uiState.value.mangaList + it.items
                    } else {
                        it.items
                    }.distinctBy { manga -> manga.id }

                    Log.d("LibraryViewModel", "list: ${followedMangaList.size}, ${
                        followedMangaList.joinToString(
                            ","
                        ) { m -> m.id }
                    }")

                    _uiState.value = _uiState.value.copy(
                        mangaList = followedMangaList,
                        loading = false,
                        currentPage = page,
                        maxPages = max(
                            getMaxPages(it.total),
                            1
                        ),
                        limit = min(it.total, 200),
                    )
                }
                .onError {
                    it.printStackTrace()
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = SimpleUIError(
                            title = "Error loading library",
                            throwable = it
                        )
                    )
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
        val mangaList: MangaList = emptyList(),
        val loading: Boolean = true,
        val currentPage: Int = 0,
        val maxPages: Int = 0,
        val limit: Int = -1,
        val error: UIError? = null,
    )
}