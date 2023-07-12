package com.blanktheevil.mangareader.domain

import com.blanktheevil.mangareader.data.stores.ChapterFeedDataStore
import com.blanktheevil.mangareader.data.stores.FollowedMangaDataStore
import com.blanktheevil.mangareader.data.stores.MangaDetailDataStore
import com.blanktheevil.mangareader.data.stores.PopularFeedDataStore
import com.blanktheevil.mangareader.data.stores.RecentFeedDataStore
import com.blanktheevil.mangareader.data.stores.SeasonalFeedDataStore
import com.blanktheevil.mangareader.data.stores.UserDataStore
import com.blanktheevil.mangareader.data.stores.UserListsDataStore

typealias ChapterFeedState = ChapterFeedDataStore.State
typealias FollowedMangaState = FollowedMangaDataStore.State
typealias PopularFeedState = PopularFeedDataStore.State
typealias UserDataState = UserDataStore.State
typealias MangaDetailState = MangaDetailDataStore.State
typealias SeasonalFeedState = SeasonalFeedDataStore.State
typealias UserListsState = UserListsDataStore.State
typealias RecentFeedState = RecentFeedDataStore.State