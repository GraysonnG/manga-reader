package com.blanktheevil.mangareader

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.mangareader.di.dataStoresModule
import com.blanktheevil.mangareader.di.stubModule
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

val LocalNavController =
    compositionLocalOf<NavHostController> { error("No NavController") }
val LocalWindow =
    compositionLocalOf<Window> { error("No Window") }

@Composable
fun DefaultPreview(block: @Composable () -> Unit) {
    val context = LocalContext.current

    if (GlobalContext.getOrNull() == null) {
        startKoin {
            androidContext(context)

            modules(
                stubModule,
                dataStoresModule,
            )
        }
    }

    MangaReaderTheme {
        CompositionLocalProvider(
            LocalNavController provides rememberNavController(),
            content = block
        )
    }
}