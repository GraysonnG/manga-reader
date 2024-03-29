package com.blanktheevil.mangareader.di

import android.content.Context
import androidx.room.Room
import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.api.GithubApi
import com.blanktheevil.mangareader.api.MangaDexApi
import com.blanktheevil.mangareader.data.MangaDexRepository
import com.blanktheevil.mangareader.data.MangaDexRepositoryImpl
import com.blanktheevil.mangareader.data.dto.RelationshipList
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.history.HistoryManagerImpl
import com.blanktheevil.mangareader.data.reader.ReaderManager
import com.blanktheevil.mangareader.data.reader.ReaderManagerImpl
import com.blanktheevil.mangareader.data.room.InkDatabase
import com.blanktheevil.mangareader.data.session.EncryptedSessionManager
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.ui.UIManager
import com.blanktheevil.mangareader.viewmodels.HistoryViewModel
import com.blanktheevil.mangareader.viewmodels.HomeViewModel
import com.blanktheevil.mangareader.viewmodels.LibraryViewModel
import com.blanktheevil.mangareader.viewmodels.ListsScreenViewModel
import com.blanktheevil.mangareader.viewmodels.LoginScreenViewModel
import com.blanktheevil.mangareader.viewmodels.MangaDetailViewModel
import com.blanktheevil.mangareader.viewmodels.SearchScreenViewModel
import com.blanktheevil.mangareader.viewmodels.UpdatesScreenViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.Date

const val MANGADEX_BASE_URL = "https://api.mangadex.org"
const val GITHUB_BASE_URL = "https://antsylich.github.io"

val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            InkDatabase::class.java,
            InkDatabase.NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        OkHttpClient.Builder().build()
    }

    single {
        Moshi.Builder()
            .add(JSONObject::class.java, JSONObjectAdapter())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(RelationshipList::class.java, RelationshipList.Adapter())
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
        HistoryManagerImpl(
            moshi = get(),
            sharedPrefs = androidContext().getSharedPreferences(
                HistoryManager.HISTORY_KEY,
                Context.MODE_PRIVATE
            ),
        )
    }


    singleOf(::SettingsManager)
    singleOf(::EncryptedSessionManager) { bind<SessionManager>() }
    singleOf(::UIManager)
    singleOf(::ReaderManagerImpl) { bind<ReaderManager>() }
    single { get<InkDatabase>().mangaDao() }
    single { get<InkDatabase>().chapterDao() }
    singleOf(::MangaDexRepositoryImpl) { bind<MangaDexRepository>() }

    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::ListsScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::MangaDetailViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::UpdatesScreenViewModel)
}