package com.blanktheevil.mangareader.data.stores

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.Volumes
import com.blanktheevil.mangareader.domain.MangaDetailState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MangaDetailDataStore(
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
                val mangaDetailsJob = async { getMangaDetails() }
                val mangaIsFollowedJob = async { getIsFollowing() }
                val mangaAggregateJob = async { getMangaAggregateVolumes() }
                val mangaReadMarkersJob = async { getChapterReadIds() }

                awaitAll(
                    mangaDetailsJob,
                    mangaIsFollowedJob,
                    mangaAggregateJob,
                    mangaReadMarkersJob,
                )

                _state.value = _state.value.copy(
                    loading = false,
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
            _state.value.manga?.let {
                when (mangaDexRepository.setMangaFollowed(it.id, true)) {
                    is Result.Success -> _state.value = _state.value.copy(
                        mangaIsFollowed = true,
                    )

                    is Result.Error -> {}
                }
            }
        }
    }

    fun unfollowManga() {
        dataStoreScope.launch {
            _state.value.manga?.let {
                when (mangaDexRepository.setMangaFollowed(it.id, false)) {
                    is Result.Success -> _state.value = _state.value.copy(
                        mangaIsFollowed = false,
                    )

                    is Result.Error -> {}
                }
            }
        }
    }

    private suspend fun getMangaDetails() {
        when (val result = mangaDexRepository.getManga(mangaId)) {
            is Result.Success -> _state.value = _state.value.copy(
                manga = result.data,
            )

            is Result.Error -> _state.value = _state.value.copy(
                error = SimpleUIError(
                    title = "Error fetching manga details.",
                    throwable = result.error,
                ),
            )
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

    private suspend fun getMangaAggregateVolumes() {
        when (val result = mangaDexRepository.getMangaAggregate(mangaId)) {
            is Result.Success -> _state.value = _state.value.copy(
                volumes = result.data,
            )

            is Result.Error -> _state.value = _state.value.copy(
                error = SimpleUIError(
                    title = "Error fetching manga aggregate volumes.",
                    throwable = result.error,
                ),
            )
        }
    }

    private suspend fun getChapterReadIds() {
        when (val result = mangaDexRepository.getChapterReadMarkersForManga(mangaId)) {
            is Result.Success -> _state.value = _state.value.copy(
                readIds = result.data,
            )

            is Result.Error -> _state.value = _state.value.copy(
                error = SimpleUIError(
                    title = "Error fetching read markers.",
                    throwable = result.error,
                ),
            )
        }
    }

    data class State(
        override val loading: Boolean = true,
        override val error: UIError? = null,
        val manga: Manga? = null,
        val mangaIsFollowed: Boolean = false,
        val volumes: Volumes = emptyList(),
        val readIds: List<String> = emptyList(),
    ) : DataStoreState()
}