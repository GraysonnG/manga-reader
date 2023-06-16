package com.blanktheevil.mangareader.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.Result
import com.blanktheevil.mangareader.data.dto.MangaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val mangaList: List<MangaDto> = emptyList()
)

class HomeViewModel: ViewModel() {
    private val mangaDexRepository = MangaDexRepository()
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun initViewModel(context: Context) {
        mangaDexRepository.initSessionManager(context)
    }

    fun logout() {
        mangaDexRepository.logout()
    }

    fun getMangaList() {
        if (_uiState.value.mangaList.isEmpty()) {
            viewModelScope.launch {
                val result = mangaDexRepository.getUserFollowsList()
                when (result) {
                    is Result.Success -> _uiState.value = _uiState.value.copy(
                        mangaList = result.data
                    )
                    is Result.Error -> {}
                }
            }
        }
    }
}