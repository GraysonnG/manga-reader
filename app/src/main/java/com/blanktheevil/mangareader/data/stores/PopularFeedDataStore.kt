package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.domain.PopularFeedState
import kotlinx.coroutines.launch

class PopularFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
) : DataStore<PopularFeedState>(
    State()
) {
    override fun get() {
        dataStoreScope.launch {
            when (val result = mangaDexRepository.getMangaPopular()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        mangaList = result.data.items,
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = SimpleUIError(
                            title = "Error fetching popular manga",
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
        override val error: UIError? = null,
        val mangaList: MangaList = emptyList(),
    ) : DataStoreState()
}