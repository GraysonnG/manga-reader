package com.blanktheevil.mangareader

import android.view.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.mangareader.data.session.SessionManager
import com.blanktheevil.mangareader.di.dataStoresModule
import com.blanktheevil.mangareader.di.stubModule
import com.blanktheevil.mangareader.navigation.MangaReaderDestinations
import com.blanktheevil.mangareader.ui.rememberImeState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
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

@Composable
fun rememberLoginState(): State<Boolean> {
    val sessionManager = koinInject<SessionManager>()
    return sessionManager.isLoggedIn.collectAsState()
}

private val bottomBarRoutes = listOf(
    MangaReaderDestinations.HOME(),
    MangaReaderDestinations.SEARCH(),
    MangaReaderDestinations.UPDATES(),
    MangaReaderDestinations.LISTS(),
    MangaReaderDestinations.HISTORY(),
)

@Composable
fun bottomBarVisible(): Boolean {
    val imeState by rememberImeState()
    val navController = LocalNavController.current
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    return currentBackStackEntry
        ?.destination
        ?.route in bottomBarRoutes && !imeState
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
            LocalScrollState provides rememberScrollState(),
            content = block
        )
    }
}