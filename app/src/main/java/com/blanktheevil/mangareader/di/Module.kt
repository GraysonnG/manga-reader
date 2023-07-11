package com.blanktheevil.mangareader.di

import android.content.Context
import android.util.Log
import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.data.DefaultMangaDexRepository
import com.blanktheevil.mangareader.data.GithubApi
import com.blanktheevil.mangareader.data.MangaDexApi
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.history.DefaultHistoryManager
import com.blanktheevil.mangareader.data.history.HistoryManager
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
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.Date

private const val MANGADEX_BASE_URL = "https://api.mangadex.org"
private const val GITHUB_BASE_URL = "https://antsylich.github.io"

val appModule = module {
    single {
        OkHttpClient.Builder().build()
    }

    single {
        Moshi.Builder()
            .add(JSONObject::class.java, JSONObjectAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()
    }

    single<MangaDexApi> {
        Retrofit.Builder()
            .baseUrl(MANGADEX_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create()
    }

    single<GithubApi> {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
            .create()
    }

    single<HistoryManager> {
        val ret = DefaultHistoryManager(
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
        EncryptedSessionManager(androidContext())
    }

    factory<MangaDexRepository> {
        DefaultMangaDexRepository(
            mangaDexApi = get(),
            githubApi = get(),
            sessionManager = get(),
            historyManager = get(),
        )
    }

    viewModel {
        HomeViewModel(
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
        ReaderViewModel(get(), get(), get())
    }
}