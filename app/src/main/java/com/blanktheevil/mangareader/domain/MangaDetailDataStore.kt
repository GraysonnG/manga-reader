package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.AggregateVolumeDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MangaDetailDataStore(
    private val mangaDexRepository: MangaDexRepository,
): DataStore<MangaDetailState>(
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
            CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
            _state.value.manga?.let {
                when (mangaDexRepository.setMangaFollowed(it.id)) {
                    is Result.Success -> _state.value = _state.value.copy(
                        mangaIsFollowed = true,
                    )
                    is Result.Error -> {}
                }
            }
        }
    }

    fun unfollowManga() {
        CoroutineScope(Dispatchers.IO).launch {
            _state.value.manga?.let {
                when (mangaDexRepository.setMangaUnfollowed(it.id)) {
                    is Result.Success -> _state.value = _state.value.copy(
                        mangaIsFollowed = false,
                    )
                    is Result.Error -> {}
                }
            }
        }
    }

    private suspend fun getMangaDetails() {
        when (val result = mangaDexRepository.getMangaDetails(mangaId)) {
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
        when (mangaDexRepository.getIsUserFollowingManga(mangaId)) {
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
        when (val result = mangaDexRepository.getReadChapterIdsByMangaIds(listOf(mangaId))) {
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
        val loading: Boolean = true,
        val manga: MangaDto? = null,
        val mangaIsFollowed: Boolean = false,
        val volumes: Map<String, AggregateVolumeDto> = emptyMap(),
        val readIds: List<String> = emptyList(),
        val error: UIError? = null,
    )
}