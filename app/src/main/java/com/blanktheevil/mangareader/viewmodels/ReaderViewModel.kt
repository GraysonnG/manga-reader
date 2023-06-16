package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderState(
    val currentPage: Int = 0,
    val maxPages: Int = 0,
    val pageUrls: List<String> = emptyList(),
    val loading: Boolean = true
)

class ReaderViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    fun initChapter(chapterId: String, context: Context) {
        viewModelScope.launch {
            val result = mangaDexRepository.getChapterPages(chapterId = chapterId)

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        pageUrls = result.data,
                        maxPages = result.data.size,
                    )

                    preloadImages(context = context)
                }

                is Result.Error -> {

                }
            }
        }
    }

    fun preloadImages(context: Context) {
        _uiState.value.pageUrls.map {
            val request = ImageRequest.Builder(context)
                .data(it)
                .build()
            context.imageLoader.enqueue(request)
        }
    }

    fun nextPage() {
        if (_uiState.value.currentPage < _uiState.value.maxPages - 1) {
            _uiState.value = _uiState.value.copy(
                currentPage = _uiState.value.currentPage + 1
            )
        }
    }

    fun prevPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.value = _uiState.value.copy(
                currentPage = _uiState.value.currentPage - 1
            )
        }
    }
}