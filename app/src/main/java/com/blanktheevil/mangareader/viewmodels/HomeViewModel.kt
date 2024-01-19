package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.DebouncedValue
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaList
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListType
import com.blanktheevil.mangareader.data.stores.FollowedMangaDataStore
import com.blanktheevil.mangareader.data.stores.PopularFeedDataStore
import com.blanktheevil.mangareader.data.stores.RecentFeedDataStore
import com.blanktheevil.mangareader.data.stores.SeasonalFeedDataStore
import com.blanktheevil.mangareader.data.stores.UserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val searchText: String = "",
    val searchMangaList: MangaList = emptyList(),
)

class HomeViewModel(
    private val mangaDexRepository: MangaDexRepository,
    val seasonalFeed: SeasonalFeedDataStore,
    val followedManga: FollowedMangaDataStore,
    val popularFeed: PopularFeedDataStore,
    val recentFeed: RecentFeedDataStore,
    val userData: UserDataStore,
    val mangaDao: MangaDao,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val _textInput = DebouncedValue(
        "",
        300,
        viewModelScope
    )

    val textInput = _textInput.asStateFlow()

    fun onTextChanged(newText: String) {
        _textInput.value = newText
        _uiState.value = _uiState.value.copy(searchText = newText)
        if (newText.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchMangaList = emptyList()
            )
        }
    }

    fun initViewModel() {
        userData.get()
        followedManga.get()
        popularFeed.get()
        seasonalFeed.get()
        recentFeed.get()
    }

    fun refresh() {
        viewModelScope.launch {
            mangaDao.apply {
                clearList(MangaListType.FOLLOWS)
                clearList(MangaListType.SEASONAL)
                clearList(MangaListType.RECENT)
                clearList(MangaListType.POPULAR)
            }

            followedManga.refresh()
            popularFeed.refresh()
            seasonalFeed.refresh()
            recentFeed.refresh()
        }
    }

    fun searchManga(text: String) {
        if (text.isNotEmpty()) {
            viewModelScope.launch {
                mangaDexRepository.getMangaSearch(text)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(
                            searchMangaList = it.items
                        )
                    }
                    .onError {
                        // TODO: handle error
                    }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            mangaDexRepository.logout()
        }
    }
}