package com.blanktheevil.mangareader.di

import android.content.Context
import android.util.Log
import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaDexRepositoryStub
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.history.HistoryManagerImpl
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.stub.StubMangaDao
import com.blanktheevil.mangareader.data.session.EncryptedSessionManager
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.viewmodels.HistoryViewModel
import com.blanktheevil.mangareader.viewmodels.HomeViewModel
import com.blanktheevil.mangareader.viewmodels.LibraryViewModel
import com.blanktheevil.mangareader.viewmodels.ListsScreenViewModel
import com.blanktheevil.mangareader.viewmodels.LoginScreenViewModel
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel
import com.blanktheevil.mangareader.viewmodels.ReaderViewModel
import com.blanktheevil.mangareader.viewmodels.SearchScreenViewModel
import com.blanktheevil.mangareader.viewmodels.UpdatesScreenViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.Date

val stubModule = module {
    single {
        OkHttpClient.Builder().build()
    }

    single {
        Moshi.Builder()
            .add(JSONObject::class.java, JSONObjectAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()
    }

    single<HistoryManager> {
        val ret = HistoryManagerImpl(
            moshi = get(),
            sharedPrefs = androidContext().getSharedPreferences(
                HistoryManager.HISTORY_KEY,
                Context.MODE_PRIVATE
            ),
        )
        Log.d("HistoryManager", "Created")
        ret
    }

    single {
        SettingsManager.getInstance().apply {
            init(androidContext())
        }
    }

    single<SessionManager> {
        EncryptedSessionManager(androidContext(), get())
    }

    single<MangaDao> {
        StubMangaDao()
    }

    factory<MangaDexRepository> {
        MangaDexRepositoryStub()
    }

    viewModel {
        HomeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    viewModel {
        HistoryViewModel(get(), get())
    }

    viewModel {
        LibraryViewModel(get())
    }

    viewModel {
        ListsScreenViewModel(get())
    }

    viewModel {
        LoginScreenViewModel(mangaDexRepository = get())
    }

    viewModel {
        MangaDetailViewModel(get(), get(), get())
    }

    viewModel {
        ReaderViewModel(get(), get())
    }

    viewModel {
        UpdatesScreenViewModel(get())
    }

    viewModel {
        SearchScreenViewModel(get())
    }
}