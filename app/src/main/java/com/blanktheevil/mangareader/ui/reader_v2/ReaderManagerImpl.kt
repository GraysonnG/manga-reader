package com.blanktheevil.mangareader.ui.reader_v2

import android.content.Context
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.letIfNotNull
import com.blanktheevil.mangareader.viewmodels.ReaderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReaderManagerImpl(
    private val mangaDexRepository: MangaDexRepository,
    private val settingsManager: SettingsManager,
    private val context: Context,
) : ReaderManager {
    private val _state = MutableStateFlow(ReaderManagerState())
    override val state = _state.asStateFlow()

    init {
        _state.value = _state.value.copy(
            readerType = try {
                settingsManager.readerType
            } catch (e: Exception) {
                ReaderType.PAGE
            }
        )
    }

    override fun setChapter(chapter: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.value = _state.value.copy(
                currentChapterId = chapter,
                currentPage = 0,
                currentChapterPageUrls = emptyList(),
                currentChapterLoading = true,
            )

            val getChapterData = async {
                mangaDexRepository
                    .getChapter(chapter)
                    .onSuccess { currentChapter ->
                        _state.value = _state.value.copy(
                            currentChapter = currentChapter,
                            mangaId = currentChapter.relatedMangaId
                        )
                    }
            }

            val getChapterPagesData = async {
                mangaDexRepository
                    .getChapterPages(chapter, settingsManager.dataSaver)
                    .onSuccess { pages ->
                        _state.value = _state.value.copy(
                            currentChapterPageUrls = pages,
                            currentChapterPageLoaded = List(pages.size) { false }
                                .toMutableList()
                        )
                    }
            }

            awaitAll(
                getChapterData,
                getChapterPagesData
            )

            mangaDexRepository.getManga(
                _state.value.mangaId ?: return@launch
            )
                .onSuccess { manga ->
                    _state.value = _state.value.copy(
                        manga = manga
                    )

                    if (manga.tags.any { it.equals("Long Strip", true) }) {
                        _state.value = _state.value.copy(
                            readerType = ReaderType.VERTICAL
                        )
                    }
                }

            _state.value = _state.value.copy(
                expanded = true,
                currentChapterLoading = false,
            )

            preloadChapterPages(_state.value.currentChapterPageUrls)

            _state.value.mangaId?.let { mangaId ->
                mangaDexRepository.getMangaAggregate(
                    mangaId
                )
                    .onSuccess { volumes ->
                        val volumeChapters = volumes.flatMap { volume ->
                            volume.chapters
                        }

                        _state.value = _state.value.copy(
                            chapters = volumeChapters,
                            currentVolumeChapter = volumeChapters.firstOrNull {
                                it.chapter == _state.value.currentChapter?.chapter
                            }
                        )
                    }
            }
        }
    }

    override fun closeReader() {
        _state.value = _state.value.copy(
            currentPage = 0,
            currentChapterId = null,
            currentChapter = null,
            currentChapterPageUrls = emptyList(),
            manga = null,
            mangaId = null,
        )
    }

    override fun expandReader() {
        _state.value = _state.value.copy(
            expanded = true
        )
    }

    override fun shrinkReader() {
        _state.value = _state.value.copy(
            expanded = false
        )
    }

    override fun toggleReader() {
        _state.value = _state.value.copy(
            expanded = !_state.value.expanded
        )
    }

    override fun nextPage() {
        val nextPage = (_state.value.currentPage + 1)

        if (nextPage == _state.value.currentChapterPageUrls.lastIndex) {
            markChapterRead()
        }

        when {
            nextPage < _state.value.currentChapterPageUrls.size -> {
                _state.value = _state.value.copy(
                    currentPage = nextPage
                )
            }

            else -> {
                nextChapter()
            }
        }
    }

    override fun prevPage() {
        val prevPage = (_state.value.currentPage - 1)

        if (prevPage >= 0) {
            _state.value = _state.value.copy(
                currentPage = prevPage
            )
        } else {
            prevChapter()
        }
    }

    override fun nextChapter() {
        _state.value.currentVolumeChapter?.next?.let {
            setChapter(it)
        } ?: run {
            closeReader()
        }
    }

    override fun prevChapter() {
        _state.value.currentVolumeChapter?.prev?.let {
            setChapter(it)
        } ?: run {
            closeReader()
        }
    }

    override fun markChapterRead() {
        letIfNotNull(
            _state.value.mangaId,
            _state.value.currentChapterId,
        ) { mangaId, chapterId ->
            CoroutineScope(Dispatchers.IO).launch {
                mangaDexRepository
                    .setChapterReadMarker(
                        mangaId = mangaId,
                        chapterId = chapterId,
                        read = true
                    )
            }
        }
    }

    private fun preloadChapterPages(
        urls: List<String>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val chunkedUrls = urls.chunked(4)

            for (chunkIndex in chunkedUrls.indices) {
                val requests = chunkedUrls[chunkIndex].map { url ->
                    ImageRequest.Builder(context)
                        .data(url)
                        .dispatcher(Dispatchers.IO)
                        .build()
                }

                requests.mapIndexed { index, it ->
                    val imageIndex = (chunkIndex * 4) + index

                    async {
                        val result = context.imageLoader.executeBlocking(it)
                        val updatedList = ArrayList<Boolean>(_state.value.currentChapterPageLoaded)

                        if (result is SuccessResult && _state.value.readerType == ReaderType.PAGE) {
                            _state.value = _state.value.copy(
                                currentChapterPageLoaded = updatedList
                                    .also {
                                        it[imageIndex] = true
                                    }
                            )
                        }
                    }
                }.awaitAll()
            }
        }
    }
}