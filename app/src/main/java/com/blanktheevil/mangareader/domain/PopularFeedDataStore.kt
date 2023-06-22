package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PopularFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
): DataStore<PopularFeedDataStore.State>(
    State()
) {
    override fun get(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            when (val result = mangaDexRepository.getPopularMangaList()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        mangaList = result.data.data,
                    )
                }

                is Result.Error -> {
                    // todo: handle error
                }
            }
        }
    }

    data class State(
        val mangaList: List<MangaDto> = emptyList(),
        val loading: Boolean = true,
    )
}