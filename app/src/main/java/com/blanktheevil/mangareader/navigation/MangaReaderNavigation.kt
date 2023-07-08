package com.blanktheevil.mangareader.navigation

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.screens.HistoryScreen
import com.blanktheevil.mangareader.ui.screens.HomeScreen
import com.blanktheevil.mangareader.ui.screens.LandingScreen
import com.blanktheevil.mangareader.ui.screens.LibraryScreen
import com.blanktheevil.mangareader.ui.screens.LibraryType
import com.blanktheevil.mangareader.ui.screens.ListsScreen
import com.blanktheevil.mangareader.ui.screens.LoginScreen
import com.blanktheevil.mangareader.ui.screens.MangaDetailScreen
import com.blanktheevil.mangareader.ui.screens.ReaderScreen
import com.blanktheevil.mangareader.ui.screens.UpdatesScreen
import com.blanktheevil.mangareader.ui.theme.slideIn
import com.blanktheevil.mangareader.ui.theme.slideOut
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

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
    ;

    operator fun invoke(
        arguments: Map<String, String>,
    ): String = "${route}${
        arguments
            .map { "${it.key}=${it.value}" }
            .joinToString(prefix= "?", separator = "&")
    }"

    operator fun invoke(
        arguments: List<String>,
    ): String = invoke(arguments.associateWith { "{$it}" })

    operator fun invoke(
        argument: String
    ) = invoke(listOf(argument))

    operator fun invoke(): String = invoke(emptyMap())
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrimaryNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
) {
    AnimatedNavHost(
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
            LandingScreen(
                navigateToHome = navController::navigateToHome,
                navigateToLogin = navController::navigateToLogin,
            )
        }

        composable(
            MangaReaderDestinations.LOGIN(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            LoginScreen(
                setTopAppBarState = setTopAppBarState,
                navigateToHome = navController::navigateToHome
            )
        }
        composable(
            MangaReaderDestinations.HOME(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            HomeScreen(
                setTopAppBarState = setTopAppBarState,
                navigateToLogin = navController::navigateToLogin,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
                navigateToLibraryScreen = navController::navigateToLibraryScreen,
            )
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
                setTopAppBarState = setTopAppBarState,
                navigateBack = navController::popBackStackOrGoHome,
                navigateToReader = navController::navigateToReader
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
            ReaderScreen(
                chapterId = it.arguments?.getString("chapterId"),
                navigateToMangaDetailScreen = navController::navigateToMangaDetailScreen,
                navigateBack = navController::popBackStackOrGoHome,
                setTopAppBarState = setTopAppBarState,
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
                setTopAppBarState = setTopAppBarState,
                libraryType = LibraryType.fromString(it.arguments?.getString("libraryType")),
                navigateToMangaDetailScreen = navController::navigateToMangaDetailScreen,
                navigateBack = navController::popBackStack,
            )
        }

        composable(
            route = MangaReaderDestinations.UPDATES(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            UpdatesScreen(
                setTopAppBarState = setTopAppBarState,
                navigateToReader = navController::navigateToReader,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
            )
        }

        composable(
            route = MangaReaderDestinations.HISTORY(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            HistoryScreen(
                setTopAppBarState = setTopAppBarState,
                navigateToReader = navController::navigateToReader,
            )
        }

        composable(
            route = MangaReaderDestinations.LISTS(),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            ListsScreen(
                setTopAppBarState = setTopAppBarState,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
            )
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

fun NavController.navigateToMangaDetailScreen(mangaId: String, popup: Boolean = false) {
    navigate(
        route = MangaReaderDestinations.MANGA_DETAIL(mapOf("mangaId" to mangaId))
    ) {
        if (popup) { popUpTo(MangaReaderDestinations.HOME()) }
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
    navigate(route = MangaReaderDestinations.LIBRARY(
        mapOf("libraryType" to libraryType.name)
    ))
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