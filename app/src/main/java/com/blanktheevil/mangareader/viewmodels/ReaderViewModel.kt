package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

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

    // TODO: get the whole response in here because you want volumes and chapters
    private var chapters: Map<String, AggregateChapterDto> = emptyMap()
    private var currentChapter: Map.Entry<String, AggregateChapterDto>? = null

    fun initReader(chapterId: String, mangaId: String, context: Context) {
        viewModelScope.launch {
            loadChapters(
                mangaId = mangaId,
            )

            loadChapter(
                chapterId = chapterId,
                context = context,
            )
        }
    }

    private suspend fun loadChapter(chapterId: String, context: Context) {
        when (val result = mangaDexRepository.getChapterPages(chapterId = chapterId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    currentPage = 0,
                    pageUrls = result.data,
                    maxPages = result.data.size,
                    loading = false,
                )

                preloadImages(context = context)
            }

            is Result.Error -> {

            }
        }

        // load the chapters

        currentChapter = chapters.entries.firstOrNull { it.value.id == chapterId }
    }

    private suspend fun loadChapters(mangaId: String) {
        when (val result = mangaDexRepository.getMangaAggregateChapters(mangaId)) {
            is Result.Success -> {
                chapters = result.data
            }

            is Result.Error -> {
                //TODO: Handle Error
            }
        }
    }

    private fun preloadImages(context: Context) {
        _uiState.value.pageUrls.map {
            val request = ImageRequest.Builder(context)
                .data(it)
                .build()
            context.imageLoader.enqueue(request)
        }
    }

    private fun nextPage() {
        _uiState.value = _uiState.value.copy(
            currentPage = _uiState.value.currentPage + 1
        )
    }

    fun prevPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.value = _uiState.value.copy(
                currentPage = _uiState.value.currentPage - 1
            )
        }
    }

    fun nextButtonClicked(
        context: Context,
        goToMangaDetail: () -> Unit
    ) {
        val currentPage = _uiState.value.currentPage
        val maxPages = _uiState.value.maxPages
        val isLatestChapter = chapters.entries.first() == currentChapter

        when {
            currentPage < (maxPages - 1) -> {
                nextPage()
            }
            isLatestChapter && currentPage == (maxPages - 1) -> {
                goToMangaDetail()
            }
            !isLatestChapter && currentPage == (maxPages - 1) -> {
                nextChapter(context = context)
            }
        }
    }

    private fun nextChapter(context: Context) {
        val nextChapterIndex = max(
            0,
            chapters.entries.indexOf(currentChapter).minus(1),
        )

        val nextChapterId = chapters.values.toList()[nextChapterIndex].id

        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            loadChapter(nextChapterId, context)
        }
    }
}