package com.blanktheevil.mangareader

import android.view.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.di.dataStoresModule
import com.blanktheevil.mangareader.di.stubModule
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.rememberKoinInject
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

val LocalNavController =
    compositionLocalOf<NavHostController> { error("No NavController") }
val LocalWindow =
    compositionLocalOf<Window> { error("No Window") }

@OptIn(ExperimentalMaterial3Api::class)
val LocalScrollBehavior =
    compositionLocalOf<TopAppBarScrollBehavior?> { null }
val LocalScrollState =
    compositionLocalOf<ScrollState> { error("No ScrollState") }
val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState") }

fun <T : ViewModel> LocalViewModel() = compositionLocalOf<T> { error("No ViewModel") }

@Composable
fun rememberLoginState(): State<Boolean> {
    val sessionManager = rememberKoinInject<SessionManager>()
    return sessionManager.isLoggedIn.collectAsState()
}

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