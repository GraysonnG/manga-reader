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
import com.blanktheevil.mangareader.letIfNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class ReaderState(
    val currentPage: Int = 0,
    val maxPages: Int = 0,
    val pageUrls: List<String> = emptyList(),
    val pageRequests: List<ImageRequest> = emptyList(),
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
        mangaDexRepository.initRepositoryManagers(context = context)
        viewModelScope.launch {
            val mangaJob = async { loadManga(mangaId = mangaId) }
            val chaptersJob = async { loadChapters(mangaId = mangaId) }
            val chapterJob = async { loadChapter(chapterId = chapterId, context = context) }

            mangaJob.await()
            chaptersJob.await()
            chapterJob.await()
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
//                    pageRequests = preloadImages(urls = result.data, context = context),
                    maxPages = result.data.size,
                    loading = false,
                )
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

    private fun preloadImages(urls: List<String>, context: Context): List<ImageRequest> {
        return urls.map {
            val request = ImageRequest.Builder(context)
                .data(it)
                .build()
            context.imageLoader.enqueue(request)
            request
        }
    }

    private fun nextPage() {
        _uiState.value = _uiState.value.copy(
            currentPage = _uiState.value.currentPage + 1
        )

        handleLastPageViewed()
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

    private fun handleLastPageViewed() {
        if (_uiState.value.currentPage == _uiState.value.maxPages - 1) {
            letIfNotNull(
                _uiState.value.manga,
                _uiState.value.currentChapter
            ) { manga, chapter ->
                viewModelScope.launch {
                    mangaDexRepository.markChapterAsRead(
                        mangaId = manga.id,
                        chapterId = chapter.id
                    )
                }
            }
        }
    }
}