package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.ChapterMap
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.Manga
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.data.stores.MangaDetailDataStore
import com.blanktheevil.mangareader.data.stores.UserListsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MangaDetailViewModel(
    private val mangaDexRepository: MangaDexRepository,
    private val sessionManager: SessionManager,
    val mangaDetail: MangaDetailDataStore,
    val userLists: UserListsDataStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()
    var id: String? = null

    fun loadMore() {
        val offset = _uiState.value.offset
        val limit = _uiState.value.limit

        id?.let { mangaId ->
            _uiState.value = _uiState.value.copy(loadingMore = true)

            CoroutineScope(Dispatchers.IO).launch {
                getVolumes(
                    offset = offset + limit,
                    mangaId = mangaId
                )

                _uiState.value = _uiState.value.copy(loadingMore = false)
            }
        }
    }

    fun getMangaDetails(id: String) {
        this.id = id
//        mangaDetail.getById(id)
        userLists.get()
        _uiState.value = _uiState.value.copy(loadingVolumes = true)
        CoroutineScope(Dispatchers.IO).launch {
            val getMangaJob = async {
                mangaDexRepository.getManga(id)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            manga = it,
                            loadingManga = false
                        )
                    }
                    .onError {
                        _uiState.value = _uiState.value.copy(
                            errorManga = SimpleUIError(
                                title = "Error getting manga details",
                                throwable = it
                            ),
                            loadingManga = false
                        )
                    }
            }
            val getVolumesJob = async {
                getVolumes(mangaId = id)
            }
            val getCoversJob = async {
                mangaDexRepository.getMangaCovers(id)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(covers = it)
                    }
            }

            awaitAll(
                getMangaJob,
                getVolumesJob,
                getCoversJob,
            )
        }
    }

    private suspend fun getVolumes(
        limit: Int = _uiState.value.limit,
        offset: Int = _uiState.value.offset,
        mangaId: String,
    ) {
        mangaDexRepository.getMangaFeed(
            id = mangaId,
            limit = limit,
            offset = offset,
            authenticated = sessionManager.isLoggedIn.value,
        ).onSuccess {
            _uiState.value = _uiState.value.copy(
                offset = offset,
                limit = limit,
                volumes = _uiState.value.volumes + it.volumes,
                total = it.totalChapters,
                loadingVolumes = false,
                loadedAllVolumes = limit + offset >= it.totalChapters,
            )
        }.onError {
            _uiState.value = _uiState.value.copy(
                errorVolumes = SimpleUIError("Error loading volumes", it),
                loadingVolumes = false
            )
        }
    }

    data class State(
        val limit: Int = 96,
        val offset: Int = 0,
        val total: Int = 0,
        val loadingManga: Boolean = true,
        val loadingVolumes: Boolean = true,
        val loadingMore: Boolean = false,
        val loadedAllVolumes: Boolean = false,
        val manga: Manga? = null,
        val volumes: Map<String, MutableMap<String, ChapterMap>> = HashMap(),
        val covers: Map<String, String> = HashMap(),
        val errorVolumes: UIError? = null,
        val errorManga: UIError? = null,
    )
}