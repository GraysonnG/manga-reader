package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ChapterFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
): DataStore<ChapterFeedDataStore.State>(
    State()
) {
    override fun get(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            getFollowsChapterList { mangaIds, chapters ->
                getMangaList(mangaIds) { manga ->
                    getReadMarkers(mangaIds) { readChapters ->
                        _state.value = _state.value.copy(
                            loading = false,
                            mangaList = manga,
                            chapterList = chapters,
                            readChapters = readChapters,
                        )
                    }
                }
            }
        }
    }

    private suspend fun getFollowsChapterList(
        onSuccess: suspend (mangaIds: List<String>, result: List<ChapterDto>) -> Unit
    ) {
        when (val result = mangaDexRepository.getUserFollowsChapterList()) {
            is Result.Success -> {
                val ids = result.data.mapNotNull { chapter ->
                    chapter.relationships.firstOrNull { it.type == "manga" }
                }.mapNotNull { it.id }
                onSuccess(ids, result.data)
            }

            is Result.Error -> {
                // TODO: handle error case
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
                // TODO: handle error state
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
                // TODO: handle error state
            }
        }
    }

    data class State(
        val loading: Boolean = true,
        val mangaList: List<MangaDto> = emptyList(),
        val chapterList: List<ChapterDto> = emptyList(),
        val readChapters: List<String> = emptyList(),
    )
}