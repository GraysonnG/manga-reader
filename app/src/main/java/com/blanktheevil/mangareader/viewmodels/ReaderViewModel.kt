package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.max

data class ReaderState(
    val currentPage: Int = 0,
    val maxPages: Int = 0,
    val pageUrls: List<String> = emptyList(),
    val loading: Boolean = true,
    val manga: MangaDto? = null,
    val chapters: List<AggregateChapterDto> = emptyList(),
    val currentChapter: ChapterDto? = null,
)

class ReaderViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    // TODO: get the whole response in here because you want volumes and chapters
    private var chapters: Map<String, AggregateChapterDto> = emptyMap()
    private var currentChapter: Map.Entry<String, AggregateChapterDto>? = null
    private var endOfFeedListener: () -> Unit = {}

    fun initReader(chapterId: String, mangaId: String, context: Context) {
        viewModelScope.launch {
            loadManga(
                mangaId = mangaId
            )

            loadChapters(
                mangaId = mangaId,
            )

            loadChapter(
                chapterId = chapterId,
                context = context,
            )
        }
    }

    fun setOnEndOfFeedListener(listener: () -> Unit) {
        endOfFeedListener = listener
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

        //load chapter by id
        when (val result = mangaDexRepository.getChapterById(id = chapterId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    currentChapter = result.data
                )
            }

            is Result.Error -> {

            }
        }

        currentChapter = chapters.entries.firstOrNull { it.value.id == chapterId }
    }

    private suspend fun loadChapters(mangaId: String) {
        when (val result = mangaDexRepository.getMangaAggregateChapters(mangaId)) {
            is Result.Success -> {
                chapters = result.data
                _uiState.value = _uiState.value.copy(
                    chapters = result.data.values.toList()
                )
            }

            is Result.Error -> {
                //TODO: Handle Error
            }
        }
    }

    private suspend fun loadManga(mangaId: String) {
        when (val result = mangaDexRepository.getMangaDetails(id = mangaId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    manga = result.data
                )
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
        context: Context
    ) {
        val currentPage = _uiState.value.currentPage
        val maxPages = _uiState.value.maxPages
        val isLatestChapter = chapters.entries.first() == currentChapter

        when {
            currentPage < (maxPages - 1) -> {
                nextPage()
            }
            isLatestChapter && currentPage == (maxPages - 1) -> {
                endOfFeedListener()
            }
            !isLatestChapter && currentPage == (maxPages - 1) -> {
                nextChapter(context = context)
            }
        }
    }

    fun nextChapter(context: Context) {
        val isLatestChapter = chapters.entries.first() == currentChapter

        if (isLatestChapter) {
            endOfFeedListener()

            return
        }

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

    fun prevChapter(context: Context) {
        val prevChapterIndex = min(
            chapters.entries.indexOf(currentChapter).plus(1),
            chapters.entries.size - 1,
        )

        val prevChapterId = chapters.values.toList()[prevChapterIndex].id

        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            loadChapter(prevChapterId, context)
        }
    }
}