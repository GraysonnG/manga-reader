package com.blanktheevil.mangareader.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Up
                )
            }
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
            )
        }
        composable(
            "$READER?chapterId={chapterId}",
            arguments = listOf(
                navArgument("chapterId") { nullable = false }
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

fun NavController.navigateToMangaDetailScreen(mangaId: String) {
    navigate(route = "$MANGA_DETAIL?mangaId=${mangaId}")
}

fun NavController.navigateToReader(chapterId: String) {
    navigate(route = "$READER?chapterId=${chapterId}")
}