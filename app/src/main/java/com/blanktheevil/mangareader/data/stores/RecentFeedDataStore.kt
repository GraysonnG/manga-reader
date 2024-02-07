package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.domain.RecentFeedState
import kotlinx.coroutines.launch

class RecentFeedDataStore(
    private val mangaDexRepository: MangaDexRepository
) : DataStore<RecentFeedState>(State()) {

    override fun get() {
        dataStoreScope.launch {
            mangaDexRepository.getMangaRecent()
                .onSuccess {
                    _state.value = _state.value.copy(
                        loading = false,
                        list = it.items
                    )
                }
                .onError {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = SimpleUIError(
                            title = "Error fetching recent manga",
                            throwable = it
                        )
                    )
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
        val list: MangaList = emptyList(),
    ) : DataStoreState()
}