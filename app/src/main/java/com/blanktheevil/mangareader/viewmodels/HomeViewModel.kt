package com.blanktheevil.mangareader.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.models.MangaListType
import com.blanktheevil.mangareader.data.stores.FollowedMangaDataStore
import com.blanktheevil.mangareader.data.stores.PopularFeedDataStore
import com.blanktheevil.mangareader.data.stores.RecentFeedDataStore
import com.blanktheevil.mangareader.data.stores.SeasonalFeedDataStore
import com.blanktheevil.mangareader.data.stores.UserDataStore
import kotlinx.coroutines.launch


class HomeViewModel(
    private val mangaDexRepository: MangaDexRepository,
    private val mangaDao: MangaDao,
    val seasonalFeed: SeasonalFeedDataStore,
    val followedManga: FollowedMangaDataStore,
    val popularFeed: PopularFeedDataStore,
    val recentFeed: RecentFeedDataStore,
    val userData: UserDataStore,
) : ViewModel() {

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

    fun logout() {
        viewModelScope.launch {
            mangaDexRepository.logout()
        }
    }
}