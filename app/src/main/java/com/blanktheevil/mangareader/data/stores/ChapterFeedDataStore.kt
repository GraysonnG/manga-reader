package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.ChapterFeedItems
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.domain.ChapterFeedState
import kotlinx.coroutines.launch

class ChapterFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
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
            mangaDexRepository.getChapterListFollows(
                limit = limit,
                offset = offset,
            ).onSuccess {
                _state.value = _state.value.copy(
                    loading = false,
                    chapterFeedItems = it.data,
                    total = it.total
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
        val chapterFeedItems: ChapterFeedItems = emptyMap(),
        val limit: Int = 15,
        val offset: Int = 0,
        val total: Int = -1,
    ) : DataStoreState()
}