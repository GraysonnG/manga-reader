package com.blanktheevil.mangareader.di

import android.content.Context
import com.blanktheevil.mangareader.adapters.JSONObjectAdapter
import com.blanktheevil.mangareader.data.MangaDexRepositoryStub
import com.blanktheevil.mangareader.data.history.HistoryManager
import com.blanktheevil.mangareader.data.history.HistoryManagerImpl
import com.blanktheevil.mangareader.data.reader.ReaderManager
import com.blanktheevil.mangareader.data.reader.ReaderManagerImpl
import com.blanktheevil.mangareader.data.room.dao.MangaDao
import com.blanktheevil.mangareader.data.room.stub.StubMangaDao
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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
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
    singleOf(::StubMangaDao) { bind<MangaDao>() }
    singleOf(::ReaderManagerImpl) { bind<ReaderManager>() }

    factoryOf(::MangaDexRepositoryStub)

    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::ListsScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::MangaDetailViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::UpdatesScreenViewModel)
}