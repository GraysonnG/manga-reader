package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.domain.FollowedMangaState
import kotlinx.coroutines.launch

class FollowedMangaDataStore(
    private val mangaDexRepository: MangaDexRepository,
) : DataStore<FollowedMangaState>(
    State()
) {
    override fun get() {
        dataStoreScope.launch {
            when (val result = mangaDexRepository.getMangaFollows()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        list = result.data.items
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = SimpleUIError(
                            title = "Error fetching followed manga",
                            throwable = result.error,
                        )
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

    data class State(
        override val loading: Boolean = true,
        val list: MangaList = emptyList(),
        val error: UIError? = null,
    ) : DataStoreState()
}