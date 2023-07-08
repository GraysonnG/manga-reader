package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.UserListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserListsDataStore(
    private val mangaDexRepository: MangaDexRepository
): DataStore<UserListsState>(State()) {
    override fun get() {
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = mangaDexRepository.getUserLists()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        data = result.data
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = SimpleUIError(
                            "Error getting user lists",
                            result.error
                        ),
                    )
                }
            }
        }
    }

    fun addMangaToList(
        mangaId: String,
        listId: String,
        onSuccess: () -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mangaDexRepository.addMangaToList(mangaId, listId)
            if (result is Result.Success) {
                onSuccess()
            } else {
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        "Error adding manga to list",
                        Exception("Error adding manga to list")
                    ),
                )
            }
        }
    }

    fun removeMangaFromList(
        mangaId: String,
        listId: String,
        onSuccess: () -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = mangaDexRepository.removeMangaFromList(mangaId, listId)
            if (result is Result.Success) {
                onSuccess()
            } else {
                _state.value = _state.value.copy(
                    error = SimpleUIError(
                        "Error removing manga from list",
                        Exception("Error removing manga from list")
                    ),
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
        val loading: Boolean = true,
        val data: Map<UserListDto, List<String>> = emptyMap(),
        val error: UIError? = null,
    )
}