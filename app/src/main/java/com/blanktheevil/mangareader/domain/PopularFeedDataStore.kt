package com.blanktheevil.mangareader.domain

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.SimpleUIError
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.data.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PopularFeedDataStore(
    private val mangaDexRepository: MangaDexRepository,
    private val viewModelScope: CoroutineScope,
): DataStore<PopularFeedState>(
    State()
) {
    override fun get() {
        viewModelScope.launch {
            when (val result = mangaDexRepository.getPopularMangaList()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        mangaList = result.data.data,
                    )
                }

                is Result.Error -> {
                    _state.value = _state.value.copy(
                        error = SimpleUIError(
                            title = "Error fetching popular manga",
                            throwable = result.error,
                        )
                    )
                }
            }
        }
    }

    data class State(
        val mangaList: List<MangaDto> = emptyList(),
        val loading: Boolean = true,
        val error: UIError? = null,
    )
}