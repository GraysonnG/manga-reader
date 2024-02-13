package com.blanktheevil.mangareader

import android.app.Application
import coil.imageLoader
import com.blanktheevil.mangareader.di.appModule
import com.blanktheevil.mangareader.di.dataStoresModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MangaReaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MangaReaderApplication)
            modules(
                appModule,
                dataStoresModule,
            )
        }
    }
}