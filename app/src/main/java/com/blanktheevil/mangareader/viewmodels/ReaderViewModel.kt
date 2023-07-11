package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.ChapterPagesDataDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.getChapters
import com.blanktheevil.mangareader.data.dto.getMangaRelationship
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.letIfNotNull
import com.squareup.moshi.Moshi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class ReaderState(
    val readerType: ReaderType = try {
        SettingsManager.getInstance().readerType
    } catch (e: Exception) {
        ReaderType.PAGE
    },
    val currentPage: Int = 0,
    val maxPages: Int = 0,
    val pageUrls: List<String> = emptyList(),
    val pageRequests: List<ImageRequest> = emptyList(),
    val loading: Boolean = true,
    val manga: MangaDto? = null,
    val chapters: List<AggregateChapterDto> = emptyList(),
    val currentChapter: ChapterDto? = null,
)

enum class ReaderType {
    PAGE,
    VERTICAL,
    HORIZONTAL,
}

class ReaderViewModel(
    private val mangaDexRepository: MangaDexRepository,
    private val settingsManager: SettingsManager,
    private val moshi: Moshi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    // TODO: get the whole response in here because you want volumes and chapters
    private var chapters: Map<String, AggregateChapterDto> = emptyMap()
    private var currentChapter: Map.Entry<String, AggregateChapterDto>? = null
    private var endOfFeedListener: () -> Unit = {}

    fun initReader(chapterId: String) {
        viewModelScope.launch {
            val chapterJob = async {
                loadChapter(chapterId = chapterId, onSuccess = {
                    it?.let { mangaId -> loadChapters(mangaId = mangaId) }
                })
            }

            chapterJob.await()
        }
    }

    fun setOnEndOfFeedListener(listener: () -> Unit) {
        endOfFeedListener = listener
    }

    private suspend fun loadChapter(
        chapterId: String,
        onSuccess: suspend (mangaId: String?) -> Unit = {}
    ) {
        //load chapter by id
        when (val result = mangaDexRepository.getChapter(chapterId = chapterId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    currentChapter = result.data.data,
                    manga = result.data.data.getMangaRelationship(moshi)
                )
                onSuccess(result.data.data.getMangaRelationship(moshi)?.id)
            }

            is Result.Error -> {

            }
        }

        when (val result = mangaDexRepository.getChapterPages(chapterId = chapterId)) {
            is Result.Success -> {
                val dataSaver = settingsManager.dataSaver
                val data = convertDataToUrl(
                    result.data.baseUrl,
                    dataSaver,
                    result.data.chapter
                )

                _uiState.value = _uiState.value.copy(
                    currentPage = 0,
                    pageUrls = data,
                    maxPages = data.size,
                    loading = false,
                )
            }

            is Result.Error -> {

            }
        }

        currentChapter = chapters.entries.firstOrNull { it.value.id == chapterId }
    }

    private suspend fun loadChapters(mangaId: String) {
        when (val result = mangaDexRepository.getMangaAggregate(mangaId)) {

            is Result.Success -> {
                val data = result.data.getChapters()

                chapters = data

                _uiState.value = _uiState.value.copy(
                    chapters = data.values.toList()
                )
            }

            is Result.Error -> {
                //TODO: Handle Error
            }
        }
    }

    private fun nextPage() {
        _uiState.value = _uiState.value.copy(
            currentPage = _uiState.value.currentPage + 1
        )

        if (_uiState.value.currentPage == _uiState.value.maxPages - 1) {
            onLastPageViewed()
        }
    }

    fun prevPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.value = _uiState.value.copy(
                currentPage = _uiState.value.currentPage - 1
            )
        }
    }

    fun nextButtonClicked() {
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
                nextChapter()
            }
        }
    }

    fun nextChapter() {
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
            loadChapter(nextChapterId)
        }
    }

    fun prevChapter() {
        val prevChapterIndex = min(
            chapters.entries.indexOf(currentChapter).plus(1),
            chapters.entries.size - 1,
        )

        val prevChapterId = chapters.values.toList()[prevChapterIndex].id

        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            loadChapter(prevChapterId)
        }
    }

    fun onLastPageViewed() {
        letIfNotNull(
            _uiState.value.manga,
            _uiState.value.currentChapter
        ) { manga, chapter ->
            mangaDexRepository.insertItemInHistory(
                mangaId = manga.id,
                chapterId = chapter.id
            )
            viewModelScope.launch {
                mangaDexRepository.setChapterReadMarker(
                    mangaId = manga.id,
                    chapterId = chapter.id,
                    read = true
                )
            }
        }
    }

    fun selectReaderType(index: Int) {
        val readerType = ReaderType.values()[index]
        _uiState.value = _uiState.value.copy(
            readerType = readerType
        )
        settingsManager.readerType = readerType
    }

    private fun convertDataToUrl(
        baseUrl: String,
        dataSaver: Boolean,
        data: ChapterPagesDataDto,
    ): List<String> {
        val imageQuality = if (dataSaver && data.dataSaver != null) "data-saver" else "data"
        return (if (dataSaver && data.dataSaver != null) data.dataSaver else data.data)?.map {
            "${baseUrl}/${imageQuality}/${data.hash}/$it"
        } ?: emptyList()
    }
}