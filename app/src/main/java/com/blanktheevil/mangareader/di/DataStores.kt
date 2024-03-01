package com.blanktheevil.mangareader.di

import com.blanktheevil.mangareader.data.stores.ChapterFeedDataStore
import com.blanktheevil.mangareader.data.stores.FollowedMangaDataStore
import com.blanktheevil.mangareader.data.stores.MangaFollowDataStore
import com.blanktheevil.mangareader.data.stores.PopularFeedDataStore
import com.blanktheevil.mangareader.data.stores.RecentFeedDataStore
import com.blanktheevil.mangareader.data.stores.SeasonalFeedDataStore
import com.blanktheevil.mangareader.data.stores.UserDataStore
import com.blanktheevil.mangareader.data.stores.UserListsDataStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataStoresModule = module {
    factoryOf(::ChapterFeedDataStore)
    factoryOf(::FollowedMangaDataStore)
    factoryOf(::MangaFollowDataStore)
    factoryOf(::PopularFeedDataStore)
    factoryOf(::RecentFeedDataStore)
    factoryOf(::SeasonalFeedDataStore)
    factoryOf(::UserDataStore)
    factoryOf(::UserListsDataStore)
}