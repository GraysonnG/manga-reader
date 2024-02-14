package com.blanktheevil.mangareader.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.ui.reader_v2.ReaderManager
import com.blanktheevil.mangareader.ui.screens.HistoryScreen
import com.blanktheevil.mangareader.ui.screens.HomeScreen
import com.blanktheevil.mangareader.ui.screens.LandingScreen
import com.blanktheevil.mangareader.ui.screens.LibraryScreen
import com.blanktheevil.mangareader.ui.screens.LibraryType
import com.blanktheevil.mangareader.ui.screens.ListsScreen
import com.blanktheevil.mangareader.ui.screens.LoginScreen
import com.blanktheevil.mangareader.ui.screens.MangaDetailScreen
import com.blanktheevil.mangareader.ui.screens.ReaderScreen
import com.blanktheevil.mangareader.ui.screens.SearchScreen
import com.blanktheevil.mangareader.ui.screens.UpdatesScreen
import com.blanktheevil.mangareader.ui.theme.slideIn
import com.blanktheevil.mangareader.ui.theme.slideOut
import org.koin.compose.koinInject

enum class MangaReaderDestinations(
    private val route: String,
) {
    LANDING("Landing"),
    LOGIN("Login"),
    HOME("Home"),
    MANGA_DETAIL("Manga_Detail"),
    READER("Reader"),
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

    navController.addOnDestinationChangedListener { _, _, _ ->
        keyboardController?.hide()
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MangaReaderDestinations.LANDING()
    ) {
        composable(
            route = MangaReaderDestinations.LANDING(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            LandingScreen()
        }

        composable(
            MangaReaderDestinations.LOGIN(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            LoginScreen()
        }
        composable(
            MangaReaderDestinations.HOME(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            HomeScreen()
        }
        composable(
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
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            MangaDetailScreen(
                mangaId = it.arguments?.getString("mangaId") ?: "null",
            )
        }
        composable(
            MangaReaderDestinations.READER(listOf("chapterId")),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://mangadex.org/chapter/{chapterId}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("chapterId") { nullable = false },
            ),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            val readerManager = koinInject<ReaderManager>()

            readerManager.setChapter(it.arguments?.getString("chapterId") ?: "")

            ReaderScreen(
                chapterId = it.arguments?.getString("chapterId"),
            )
        }
        composable(
            route = MangaReaderDestinations.LIBRARY("libraryType"),
            arguments = listOf(
                navArgument("libraryType") { nullable = false }
            ),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            LibraryScreen(
                libraryType = LibraryType.fromString(it.arguments?.getString("libraryType")),
            )
        }

        composable(
            route = MangaReaderDestinations.UPDATES(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            UpdatesScreen()
        }

        composable(
            route = MangaReaderDestinations.HISTORY(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            HistoryScreen()
        }

        composable(
            route = MangaReaderDestinations.LISTS(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            ListsScreen()
        }

        composable(
            route = MangaReaderDestinations.SEARCH(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            SearchScreen()
        }
    }
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

fun NavController.navigateToReader(chapterId: String) {
    navigate(
        route = MangaReaderDestinations.READER(
            mapOf(
                "chapterId" to chapterId
            )
        )
    )
}

/** TODO: Uh-oh i forget what this is used for **/
fun NavController.popBackStackOrGoHome() {
    if (previousBackStackEntry == null) {
        navigateToHome()
    } else {
        popBackStack()
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