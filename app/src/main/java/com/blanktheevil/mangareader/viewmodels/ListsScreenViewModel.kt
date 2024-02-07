package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.objects.UserListDto
import com.blanktheevil.mangareader.data.dto.utils.MangaList
import com.blanktheevil.mangareader.data.dto.utils.parseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListsScreenViewModel(
    private val mangaDexRepository: MangaDexRepository
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun initViewModel() {
        CoroutineScope(Dispatchers.IO).launch {
            val listData = mangaDexRepository.getCustomLists()
                .onError {
                    _state.value = _state.value.copy(
                        listDataLoading = false,
                        error = SimpleUIError(
                            "Error getting user lists",
                            it
                        ),
                    )
                }
                .collectOrNull()
                ?.parseData() ?: emptyMap()

            mangaDexRepository.getMangaList("lists", listData.values.flatten())
                .onSuccess {
                    _state.value = _state.value.copy(
                        mangaListsLoading = false,
                        lists = listData.entries.associate { (userList, mangaIds) ->
                            userList to it.items.filter { manga ->
                                manga.id in mangaIds
                            }
                        }
                    )
                }
                .onError {
                    _state.value = _state.value.copy(
                        mangaListsLoading = false,
                        error = SimpleUIError(
                            "Error getting manga list",
                            it
                        ),
                    )
                }

        }
    }

    data class State(
        val lists: Map<UserListDto, MangaList> = emptyMap(),
        val listDataLoading: Boolean = true,
        val mangaListsLoading: Boolean = true,
        val error: UIError? = null,
    )
}