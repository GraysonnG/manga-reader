package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FollowedMangaDataStore(
    private val mangaDexRepository: MangaDexRepository
): DataStore<FollowedMangaDataStore.State>(
    State()
) {
    override fun get(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            when(val result = mangaDexRepository.getUserFollowsList()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        list = result.data.data
                    )
                }

                is Result.Error -> {
                    // TODO: handle error
                }
            }
        }
    }

    data class State(
        val loading: Boolean = true,
        val list: List<MangaDto> = emptyList(),
    )
}