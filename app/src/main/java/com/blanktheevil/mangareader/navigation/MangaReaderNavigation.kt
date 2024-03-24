package com.blanktheevil.mangareader.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.LocalScrollState
import com.blanktheevil.mangareader.ui.screens.HistoryScreen
import com.blanktheevil.mangareader.ui.screens.HomeScreen
import com.blanktheevil.mangareader.ui.screens.LandingScreen
import com.blanktheevil.mangareader.ui.screens.LibraryScreen
import com.blanktheevil.mangareader.ui.screens.LibraryType
import com.blanktheevil.mangareader.ui.screens.ListsScreen
import com.blanktheevil.mangareader.ui.screens.LoginScreen
import com.blanktheevil.mangareader.ui.screens.MangaDetailScreen
import com.blanktheevil.mangareader.ui.screens.SearchScreen
import com.blanktheevil.mangareader.ui.screens.UpdatesScreen
import com.blanktheevil.mangareader.ui.theme.slideIn
import com.blanktheevil.mangareader.ui.theme.slideOut
import kotlinx.coroutines.launch

enum class MangaReaderDestinations(
    private val route: String,
) {
    LANDING("Landing"),
    LOGIN("Login"),
    HOME("Home"),
    MANGA_DETAIL("Manga_Detail"),
    LIBRARY("Library"),
    UPDATES("Updates"),
    HISTORY("History"),
    LISTS("Lists"),
    SEARCH("Search"),
    ;

    operator fun invoke(
        arguments: Map<String, String>,
    ): String = "${route}${
        arguments
            .map { "${it.key}=${it.value}" }
            .joinToString(prefix = "?", separator = "&")
    }"

    operator fun invoke(
        arguments: List<String>,
    ): String = invoke(arguments.associateWith { "{$it}" })

    operator fun invoke(
        argument: String
    ) = invoke(listOf(argument))

    operator fun invoke(): String = invoke(emptyMap())
}

@Composable
fun PrimaryNavGraph(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = LocalScrollState.current
    val coroutineScope = rememberCoroutineScope()

    navController.addOnDestinationChangedListener { _, _, _ ->
        keyboardController?.hide()
        coroutineScope.launch { scrollState.scrollTo(0) }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MangaReaderDestinations.LANDING()
    ) {
        simpleComposable(
            route = MangaReaderDestinations.LANDING
        ) {
            LandingScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.LOGIN
        ) {
            LoginScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.HOME
        ) {
            HomeScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.MANGA_DETAIL("mangaId"),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://mangadex.org/title/{mangaId}/.*"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("mangaId") { nullable = false }
            ),
        ) {
            MangaDetailScreen(
                mangaId = it.arguments?.getString("mangaId") ?: "null",
            )
        }

        simpleComposable(
            route = MangaReaderDestinations.LIBRARY("libraryType"),
            arguments = listOf(
                navArgument("libraryType") { nullable = false }
            ),
        ) {
            LibraryScreen(
                libraryType = LibraryType.fromString(it.arguments?.getString("libraryType")),
            )
        }

        simpleComposable(
            route = MangaReaderDestinations.UPDATES
        ) {
            UpdatesScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.HISTORY
        ) {
            HistoryScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.LISTS
        ) {
            ListsScreen()
        }

        simpleComposable(
            route = MangaReaderDestinations.SEARCH
        ) {
            SearchScreen()
        }
    }
}

fun NavGraphBuilder.simpleComposable(
    route: MangaReaderDestinations,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    composable: @Composable AnimatedContentScope.(navBackStackEntry: NavBackStackEntry) -> Unit,
) = simpleComposable(
    route = route(),
    arguments = arguments,
    deepLinks = deepLinks,
    composable = composable,
)

fun NavGraphBuilder.simpleComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    composable: @Composable AnimatedContentScope.(navBackStackEntry: NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = slideIn,
        exitTransition = slideOut,
        popEnterTransition = slideIn,
        popExitTransition = slideOut,
        content = composable,
    )
}

fun NavController.navigateToHome() {
    navigate(route = MangaReaderDestinations.HOME()) {
        popUpTo(MangaReaderDestinations.LANDING()) {
            inclusive = true
        }
    }
}

fun NavController.navigateToLogin() {
    navigate(route = MangaReaderDestinations.LOGIN()) {
        popUpTo(MangaReaderDestinations.HOME()) {
            inclusive = true
        }
    }
}

fun NavController.navigateToMangaDetailScreen(mangaId: String) {
    navigate(
        route = MangaReaderDestinations.MANGA_DETAIL(mapOf("mangaId" to mangaId))
    ) {
        popUpTo(MangaReaderDestinations.MANGA_DETAIL(mapOf("mangaId" to mangaId))) {
            inclusive = true
        }
    }
}

fun NavController.navigateToLibraryScreen(
    libraryType: LibraryType,
) {
    navigate(
        route = MangaReaderDestinations.LIBRARY(
            mapOf("libraryType" to libraryType.name)
        )
    )
}

fun NavController.navigateToUpdatesScreen() {
    navigate(route = MangaReaderDestinations.UPDATES()) {
        popUpTo(route = MangaReaderDestinations.HOME())
    }
}

fun NavController.navigateToHistoryScreen() {
    navigate(
        route = MangaReaderDestinations.HISTORY()
    ) {
        popUpTo(route = MangaReaderDestinations.HOME())
    }
}

fun NavController.navigateToListsScreen() {
    navigate(
        route = MangaReaderDestinations.LISTS()
    ) {
        popUpTo(route = MangaReaderDestinations.HOME())
    }
}

fun NavController.navigateToSearchScreen() {
    navigate(
        route = MangaReaderDestinations.SEARCH()
    ) {
        popUpTo(route = MangaReaderDestinations.HOME())
    }
}