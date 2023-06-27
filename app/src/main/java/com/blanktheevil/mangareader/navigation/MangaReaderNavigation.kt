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
import com.blanktheevil.mangareader.ui.theme.slideIn
import com.blanktheevil.mangareader.ui.theme.slideOut
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

private const val LANDING = "Landing"
private const val LOGIN = "Login"
private const val HOME = "Home"
private const val MANGA_DETAIL = "Manga_Detail"
private const val READER = "Reader"
private const val LIBRARY = "Library"

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
        startDestination = LANDING
    ) {
        composable(
            route = LANDING,
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            setTopAppBar { }
            LandingScreen(
                navigateToHome = navController::navigateToHome,
                navigateToLogin = navController::navigateToLogin,
            )
        }

        composable(
            LOGIN,
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
            HOME,
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
            )
        }
        composable(
            "$MANGA_DETAIL?mangaId={mangaId}",
            arguments = listOf(
                navArgument("mangaId") { nullable = false }
            ),
            enterTransition = slideIn,
            exitTransition = slideOut,
            popEnterTransition = slideIn,
            popExitTransition = slideOut,
        ) {
            MangaDetailScreen(
                id = it.arguments?.getString("mangaId"),
                setTopAppBar = setTopAppBar,
                popBackStack = navController::popBackStack,
                navigateToReader = navController::navigateToReader
            )
        }
        composable(
            "$READER?chapterId={chapterId}&mangaId={mangaId}",
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
            route = "$LIBRARY?libraryType={libraryType}",
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
    }
}

fun NavController.navigateToHome() {
    navigate(route = HOME) {
        popUpTo(LANDING) {
            inclusive = true
        }
    }
}

fun NavController.navigateToLogin() {
    navigate(route = LOGIN) {
        popUpTo(HOME) {
            inclusive = true
        }
    }
}

fun NavController.navigateToMangaDetailScreen(mangaId: String, popup: Boolean = false) {
    navigate(route = "$MANGA_DETAIL?mangaId=${mangaId}") {
        if (popup) { popUpTo(HOME) }
    }
}

fun NavController.navigateToReader(chapterId: String, mangaId: String) {
    navigate(route = "$READER?chapterId=${chapterId}&mangaId=${mangaId}")
}

fun NavController.navigateToLibraryScreen(
    libraryType: LibraryType,
) {
    navigate(route = "$LIBRARY?libraryType=${libraryType.name}")
}