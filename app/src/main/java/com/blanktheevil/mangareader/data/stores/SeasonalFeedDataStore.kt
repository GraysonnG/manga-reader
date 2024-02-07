package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.domain.SeasonalFeedState
import kotlinx.coroutines.launch

class SeasonalFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
) : DataStore<SeasonalFeedState>(
    State()
) {
    override fun get() {
        dataStoreScope.launch {
            getNameAndMangaIds()
        }
    }

    override fun onRefresh() {
        _state.value = _state.value.copy(
            loading = true,
            error = null,
        )
    }

    private suspend fun getNameAndMangaIds() {
        when (val result = mangaDexRepository.getMangaSeasonal()) {
            is Result.Success -> {
                _state.value = _state.value.copy(
                    name = result.data.title,
                    manga = result.data.mangaList,
                    loading = false,
                    error = null,
                )
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
        override val loading: Boolean = true,
        override val error: UIError? = null,
        val name: String = "",
        val manga: MangaList = emptyList(),
    ) : DataStoreState()
}