package com.blanktheevil.mangareader.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.blanktheevil.mangareader.ui.screens.HomeScreen
import com.blanktheevil.mangareader.ui.screens.LandingScreen
import com.blanktheevil.mangareader.ui.screens.LibraryScreen
import com.blanktheevil.mangareader.ui.screens.LibraryType
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
    setTopAppBar: (topAppBar: @Composable () -> Unit) -> Unit,
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
                setTopAppBar = setTopAppBar,
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
                setTopAppBar = setTopAppBar,
                navigateToLogin = navController::navigateToLogin,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
                navigateToReader = navController::navigateToReader,
                navigateToLibraryScreen = navController::navigateToLibraryScreen,
                navigateToUpdatesScreen = navController::navigateToUpdatesScreen,
            )
        }
        composable(
            route = MangaReaderDestinations.MANGA_DETAIL("mangaId"),
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
                setTopAppBar = setTopAppBar,
                navigateBack = navController::popBackStack,
                navigateToReader = navController::navigateToReader
            )
        }
        composable(
            MangaReaderDestinations.READER(listOf("chapterId" , "mangaId")),
            arguments = listOf(
                navArgument("chapterId") { nullable = false },
                navArgument("mangaId") { nullable = false },
            ),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            ReaderScreen(
                chapterId = it.arguments?.getString("chapterId"),
                mangaId = it.arguments?.getString("mangaId"),
                navigateToMangaDetailScreen = navController::navigateToMangaDetailScreen,
                navigateBack = navController::popBackStack,
                setTopAppBar = setTopAppBar,
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
                setTopAppBar = setTopAppBar,
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
                setTopAppBar = setTopAppBar,
                navigateToReader = navController::navigateToReader,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
                popBackStack = navController::popBackStack,
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

fun NavController.navigateToReader(chapterId: String, mangaId: String) {
    navigate(
        route = MangaReaderDestinations.READER(
            mapOf(
                "chapterId" to chapterId,
                "mangaId" to mangaId
            )
        )
    )
}

fun NavController.navigateToLibraryScreen(
    libraryType: LibraryType,
) {
    navigate(route = MangaReaderDestinations.LIBRARY(
        mapOf("libraryType" to libraryType.name)
    ))
}

fun NavController.navigateToUpdatesScreen() {
    navigate(route = MangaReaderDestinations.UPDATES())
}
