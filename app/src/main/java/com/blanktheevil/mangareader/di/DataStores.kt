package com.blanktheevil.mangareader.di

import com.blanktheevil.mangareader.data.stores.ChapterFeedDataStore
import com.blanktheevil.mangareader.data.stores.FollowedMangaDataStore
import com.blanktheevil.mangareader.data.stores.MangaFollowDataStore
import com.blanktheevil.mangareader.data.stores.PopularFeedDataStore
import com.blanktheevil.mangareader.data.stores.RecentFeedDataStore
import com.blanktheevil.mangareader.data.stores.SeasonalFeedDataStore
import com.blanktheevil.mangareader.data.stores.UserDataStore
import com.blanktheevil.mangareader.data.stores.UserListsDataStore
import org.koin.dsl.module

val dataStoresModule = module {
    factory {
        ChapterFeedDataStore(get())
    }

    factory {
        FollowedMangaDataStore(get())
    }

    factory {
        MangaFollowDataStore(get())
    }

    factory {
        PopularFeedDataStore(get())
    }

    factory {
        SeasonalFeedDataStore(get())
    }

    factory {
        UserDataStore(get())
    }

    factory {
        UserListsDataStore(get())
    }

    factory {
        RecentFeedDataStore(get())
    }
}