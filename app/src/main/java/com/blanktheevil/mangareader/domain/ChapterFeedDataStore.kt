package com.blanktheevil.mangareader.domain

import android.util.Log
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChapterFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
    private val viewModelScope: CoroutineScope,
): DataStore<ChapterFeedState>(
    ChapterFeedState()
) {
    override fun get() {
        getWithOffset(offset = 0)
    }

    fun getWithOffset(
        limit: Int = 15,
        offset: Int,
        loading: Boolean = false,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading = loading,
            )

            getFollowsChapterList(
                limit = limit,
                offset = offset,
            ) { mangaIds, data ->
                getMangaList(mangaIds) { manga ->
                    getReadMarkers(mangaIds) { readChapters ->
                        _state.value = _state.value.copy(
                            loading = false,
                            mangaList = manga,
                            chapterList = data.data,
                            readChapters = readChapters,
                            total = data.total,
                        )
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        _state.value = _state.value.copy(
            loading = true,
            error = null,
        )
    }

    private suspend fun getFollowsChapterList(
        limit: Int = 15,
        offset: Int = 0,
        onSuccess: suspend (mangaIds: List<String>, result: GetChapterListResponse) -> Unit
    ) {
        when (val result = mangaDexRepository
            .getUserFollowsChapterList(
                limit = limit,
                offset = offset,
            )) {
            is Result.Success -> {
                val ids = result.data.data.mapNotNull { chapter ->
                    chapter.relationships.firstOrNull { it.type == "manga" }
                }.mapNotNull { it.id }
                onSuccess(ids, result.data)
            }

            is Result.Error -> {
                Log.e("getFollowsChapterList", result.error.message ?: "Unknown error")
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        title = "Error fetching chapter list",
                        throwable = result.error,
                    )
                )
                retry()
            }
        }
    }

    private suspend fun getMangaList(
        mangaIds: List<String>,
        onSuccess: suspend (
            manga: List<MangaDto>,
        ) -> Unit
    ) {
        when (val mangaListResult = mangaDexRepository
            .getMangaList(ids = mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    mangaListResult.data,
                )
            }
            is Result.Error -> {
                Log.e("getMangaList", mangaListResult.error.message ?: "Unknown error")
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        title = "Error fetching manga list for chapter feed",
                        throwable = mangaListResult.error,
                    )
                )
                retry()
            }
        }
    }

    private suspend fun getReadMarkers(
        mangaIds: List<String>,
        onSuccess: suspend (
            readChapters: List<String>,
        ) -> Unit
    ) {
        when (val readChaptersResult = mangaDexRepository
            .getReadChapterIdsByMangaIds(mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    readChaptersResult.data
                )
            }

            is Result.Error -> {
                Log.e("getReadMarkers", readChaptersResult.error.message ?: "Unknown error")
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        title = "Error fetching read markers",
                        throwable = readChaptersResult.error,
                    )
                )
                retry()
            }
        }
    }

    data class State(
        val loading: Boolean = true,
        val mangaList: List<MangaDto> = emptyList(),
        val chapterList: List<ChapterDto> = emptyList(),
        val readChapters: List<String> = emptyList(),
        val limit: Int = 15,
        val offset: Int = 0,
        val total: Int = -1,
        val error: UIError? = null,
    )
}