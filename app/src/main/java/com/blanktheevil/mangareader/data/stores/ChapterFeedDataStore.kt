package com.blanktheevil.mangareader.data.stores

import android.util.Log
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.GetChapterListResponse
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.getMangaRelationship
import com.blanktheevil.mangareader.domain.ChapterFeedState
import com.blanktheevil.mangareader.ui.components.ChapterFeedItems
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch

class ChapterFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
    private val moshi: Moshi,
) : DataStore<ChapterFeedState>(
    ChapterFeedState()
) {
    override fun get() {
        getWithOffset(
            offset = _state.value.offset,
            loading = _state.value.loading
        )
    }

    fun getWithOffset(
        limit: Int = 15,
        offset: Int,
        loading: Boolean = false,
    ) {
        _state.value = _state.value.copy(
            loading = loading,
        )

        dataStoreScope.launch {
            getFollowsChapterList(
                limit = limit,
                offset = offset,
            ) { mangaIds, data ->
                getMangaList(mangaIds) { manga ->
                    getReadMarkers(mangaIds) { readChapters ->
                        val items = manga.associateWith { m ->
                            data.data
                                .filter {
                                    it.getMangaRelationship(moshi)?.id == m.id
                                }
                                .map {
                                    Pair(it, it.id in readChapters)
                                }
                        }

                        _state.value = _state.value.copy(
                            loading = false,
                            chapterFeedItems = items,
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
            .getChapterListFollows(
                limit = limit,
                offset = offset,
            )) {
            is Result.Success -> {
                val ids = result.data.data.mapNotNull { chapter ->
                    chapter.getMangaRelationship(moshi)?.id
                }
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
            .getMangaList(mangaIds = mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    mangaListResult.data.data,
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
            .getChapterReadMarkersForManga(mangaIds)) {
            is Result.Success -> {
                onSuccess(
                    readChaptersResult.data.data
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
        override val loading: Boolean = true,
        val chapterFeedItems: ChapterFeedItems = emptyMap(),
        val limit: Int = 15,
        val offset: Int = 0,
        val total: Int = -1,
        val error: UIError? = null,
    ) : DataStoreState()
}