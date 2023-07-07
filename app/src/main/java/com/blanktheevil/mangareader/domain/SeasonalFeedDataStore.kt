package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeasonalFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
): DataStore<SeasonalFeedState>(
    State()
) {
    override fun get() {
        CoroutineScope(Dispatchers.IO).launch {
            getNameAndMangaIds { name, mangaIds ->
                getMangaList(mangaIds = mangaIds) { manga ->
                    _state.value = _state.value.copy(
                        loading = false,
                        manga = manga,
                        name = name,
                    )
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

    private suspend fun getMangaList(
        mangaIds: List<String>,
        onSuccess: (manga: List<MangaDto>) -> Unit
    ) {
        when(val result = mangaDexRepository.getMangaList(ids = mangaIds)) {
            is Result.Success -> {
                onSuccess(result.data)
            }
            is Result.Error -> {
                _state.value = _state.value.copy(
                    loading = false,
                    error = null
                )
            }
        }
    }

    private suspend fun getNameAndMangaIds(onSuccess: suspend (String, List<String>) -> Unit) {
        when (val result = mangaDexRepository.getSeasonalMangaData()) {
            is Result.Success -> {
                onSuccess(result.data.name ?: "", result.data.mangaIds)
            }
            is Result.Error -> {
                _state.value = _state.value.copy(
                    loading = false,
                    error = null
                )
            }
        }
    }

    data class State(
        val loading: Boolean = true,
        val name: String = "",
        val manga: List<MangaDto> = emptyList(),
        val error: UIError? = null,
    )
}