package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import com.blanktheevil.mangareader.data.Chapter
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.VolumeChapter
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.letIfNotNull
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    val manga: Manga? = null,
    val chapters: List<VolumeChapter> = emptyList(),
    val currentChapter: Chapter? = null,
)

enum class ReaderType {
    PAGE,
    VERTICAL,
    HORIZONTAL,
}

class ReaderViewModel(
    private val mangaDexRepository: MangaDexRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    // TODO: get the whole response in here because you want volumes and chapters
    private var chapters: List<VolumeChapter> = emptyList()
    private var currentChapter: VolumeChapter? = null
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
                    currentChapter = result.data,
                    manga = result.data.relatedManga
                )
                onSuccess(result.data.relatedMangaId)
            }

            is Result.Error -> {

            }
        }

        mangaDexRepository.getChapterPages(chapterId, dataSaver = settingsManager.dataSaver)
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    currentPage = 0,
                    pageUrls = it,
                    maxPages = it.size,
                    loading = false,
                )
            }
            .onError { }

        currentChapter = chapters.firstOrNull { it.id == chapterId }
    }

    private suspend fun loadChapters(mangaId: String) {
        when (val result = mangaDexRepository.getMangaAggregate(mangaId)) {

            is Result.Success -> {
                val data = result.data.flatMap { it.chapters }

                chapters = data

                _uiState.value = _uiState.value.copy(
                    chapters = data
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
        val isLatestChapter = chapters.first().id == currentChapter?.id


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
        val isLatestChapter = currentChapter?.next == null // when next is null latest is true

        if (isLatestChapter) {
            endOfFeedListener()

            return
        }

        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            currentChapter?.next?.let { loadChapter(it) }
        }
    }

    fun prevChapter() {
        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            currentChapter?.prev?.let { loadChapter(it) }
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
}