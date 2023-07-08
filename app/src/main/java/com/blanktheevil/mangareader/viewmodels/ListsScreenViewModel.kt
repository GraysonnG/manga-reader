package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.dto.UserListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListsScreenViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun initViewModel(context: Context) {
        mangaDexRepository.initRepositoryManagers(context)

        getListData { data ->
            _state.value = _state.value.copy(
                listDataLoading = false,
            )
            when(val result = mangaDexRepository.getMangaList(data.values.flatten())) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        mangaListsLoading = false,
                        lists = data.entries.associate { (userList, mangaIds) ->
                            userList to result.data.filter { manga ->
                                manga.id in mangaIds
                            }
                        }
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        mangaListsLoading = false,
                        error = SimpleUIError(
                            "Error getting manga list",
                            result.error
                        ),
                    )
                }
            }
        }
    }

    fun getListData(onSuccess: suspend (data: Map<UserListDto, List<String>>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = mangaDexRepository.getUserLists()) {
                is Result.Success -> {
                    onSuccess(result.data)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        listDataLoading = false,
                        error = SimpleUIError(
                            "Error getting user lists",
                            result.error
                        ),
                    )
                }
            }
        }
    }

    data class State(
        val lists: Map<UserListDto, List<MangaDto>> = emptyMap(),
        val listDataLoading: Boolean = true,
        val mangaListsLoading: Boolean = true,
        val error: UIError? = null,
    )
}