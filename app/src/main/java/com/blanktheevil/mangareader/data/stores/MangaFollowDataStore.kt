package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.domain.MangaDetailState
import kotlinx.coroutines.launch

class MangaFollowDataStore(
    private val mangaDexRepository: MangaDexRepository,
) : DataStore<MangaDetailState>(
    MangaDetailState()
) {
    private var mangaId: String = "null"

    override fun get() {
        getById(mangaId)
    }

    fun getById(mangaId: String) {
        this.mangaId = mangaId

        if (this.mangaId != "null") {
            _state.value = _state.value.copy(
                loading = true,
                error = null,
            )

            dataStoreScope.launch {
                getIsFollowing()
                _state.value = _state.value.copy(
                    loading = false
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

    fun followManga() {
        dataStoreScope.launch {
            when (mangaDexRepository.setMangaFollowed(mangaId, true)) {
                is Result.Success -> _state.value = _state.value.copy(
                    mangaIsFollowed = true,
                )

                is Result.Error -> {}
            }
        }
    }

    fun unfollowManga() {
        dataStoreScope.launch {
            when (mangaDexRepository.setMangaFollowed(mangaId, false)) {
                is Result.Success -> _state.value = _state.value.copy(
                    mangaIsFollowed = false,
                )

                is Result.Error -> {}
            }
        }
    }

    private suspend fun getIsFollowing() {
        when (mangaDexRepository.getMangaFollowed(mangaId)) {
            is Result.Success -> _state.value = _state.value.copy(
                mangaIsFollowed = true,
            )

            is Result.Error -> _state.value = _state.value.copy(
                mangaIsFollowed = false,
            )
        }
    }

    data class State(
        override val loading: Boolean = true,
        override val error: UIError? = null,
        val mangaIsFollowed: Boolean = false,
    ) : DataStoreState()
}