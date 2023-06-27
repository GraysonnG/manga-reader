package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FollowedMangaDataStore(
    private val mangaDexRepository: MangaDexRepository,
    private val viewModelScope: CoroutineScope,
): DataStore<FollowedMangaState>(
    State()
) {
    override fun get() {
        viewModelScope.launch {
            when(val result = mangaDexRepository.getUserFollowsList()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        list = result.data.data
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        error = SimpleUIError(
                            title = "Error fetching followed manga",
                            throwable = result.error,
                        )
                    )
                }
            }
        }
    }

    data class State(
        val loading: Boolean = true,
        val list: List<MangaDto> = emptyList(),
        val error: UIError? = null,
    )
}