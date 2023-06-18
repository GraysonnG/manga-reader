package com.blanktheevil.mangareader.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.blanktheevil.mangareader.ui.screens.HomeScreen
import com.blanktheevil.mangareader.ui.screens.LoginScreen
import com.blanktheevil.mangareader.ui.screens.MangaDetailScreen
import com.blanktheevil.mangareader.ui.screens.ReaderScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

private const val LOGIN = "Login"
private const val HOME = "Home"
private const val MANGA_DETAIL = "Manga_Detail"
private const val READER = "Reader"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PrimaryNavGraph(
    navController: NavHostController
) {
    AnimatedNavHost(
        navController = navController, startDestination = LOGIN
    ) {
        composable(
            LOGIN,

        ) {
            LoginScreen(navigateToHome = navController::navigateToHome)
        }
        composable(
            HOME,
        ) {
            HomeScreen(
                navigateToLogin = navController::navigateToLogin,
                navigateToMangaDetail = navController::navigateToMangaDetailScreen,
                navigateToReader = navController::navigateToReader
            )
        }
        composable(
            "$MANGA_DETAIL?mangaId={mangaId}",
            arguments = listOf(
                navArgument("mangaId") { nullable = false }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right
                )
            }
        ) {
            MangaDetailScreen(
                id = it.arguments?.getString("mangaId"),
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
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right
                )
            }
        ) {
            ReaderScreen(
                chapterId = it.arguments?.getString("chapterId"),
                mangaId = it.arguments?.getString("mangaId"),
                navigateToMangaDetailScreen = navController::navigateToMangaDetailScreen
            )
        }
    }
}

fun NavController.navigateToHome() {
    navigate(route = HOME) {
        popUpTo(LOGIN) {
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