package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.LocalSnackbarHostState
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.UIError
import com.blanktheevil.mangareader.data.StubData
import com.blanktheevil.mangareader.data.dto.utils.manga.toMangaList
import com.blanktheevil.mangareader.data.stores.DataStoreState
import com.blanktheevil.mangareader.domain.FollowedMangaState
import com.blanktheevil.mangareader.domain.PopularFeedState
import com.blanktheevil.mangareader.domain.RecentFeedState
import com.blanktheevil.mangareader.domain.SeasonalFeedState
import com.blanktheevil.mangareader.navigation.navigateToLibraryScreen
import com.blanktheevil.mangareader.navigation.navigateToLogin
import com.blanktheevil.mangareader.rememberLoginState
import com.blanktheevil.mangareader.ui.PullToRefreshScreen
import com.blanktheevil.mangareader.ui.components.FeatureCarousel
import com.blanktheevil.mangareader.ui.components.HomeUserMenu
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.components.ScrollableBox
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.setTopAppBarState
import com.blanktheevil.mangareader.ui.sheets.DonationSheetLayout
import com.blanktheevil.mangareader.ui.sheets.SettingsSheetLayout
import com.blanktheevil.mangareader.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val navController = LocalNavController.current
    val seasonalFeedState by homeViewModel.seasonalFeed()
    val followedMangaState by homeViewModel.followedManga()
    val popularFeedState by homeViewModel.popularFeed()
    val recentFeedState by homeViewModel.recentFeed()
    val userDataState by homeViewModel.userData()
    var settingsSheetOpen by remember { mutableStateOf(false) }
    var donationSheetOpen by remember { mutableStateOf(false) }
    val tipIcon = painterResource(id = R.drawable.twotone_coffee_24)
    val settingsIcon = painterResource(id = R.drawable.twotone_settings_24)
    val homeIcon = painterResource(id = R.drawable.twotone_home_24)
    val loggedIn by rememberLoginState()

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = stringResource(id = R.string.home_screen_title),
            titleIcon = homeIcon,
            actions = {
                BadgedBox(badge = {
                    Badge(
                        Modifier.offset(
                            x = (-12).dp,
                            y = (12).dp,
                        ),
                        containerColor =
                        Color.Red
                    )
                }) {
                    IconButton(onClick = {
                        donationSheetOpen = true
                    }) {
                        Icon(
                            painter = tipIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    onClick = {
                        settingsSheetOpen = true
                    }
                ) {
                    Icon(painter = settingsIcon, contentDescription = null)
                }
                HomeUserMenu(
                    username = userDataState.username,
                    onLogoutClicked = {
                        homeViewModel.logout()
                    },
                    onLoginClicked = {
                        navController.navigateToLogin()
                    },
                )
            },
        )
    )

    OnMount {
        homeViewModel.initViewModel()
    }

    LaunchedEffect(loggedIn) {
        if (loggedIn) {
            homeViewModel.handleAsyncLogin()
        }
    }

    HomeScreenLayout(
        seasonalFeedState = seasonalFeedState,
        followedMangaState = followedMangaState,
        popularFeedState = popularFeedState,
        recentFeedState = recentFeedState,
        refresh = homeViewModel::refresh,
    )

    if (settingsSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 56.dp),
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = { settingsSheetOpen = false }
        ) {
            SettingsSheetLayout()
        }
    }

    if (donationSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 56.dp),
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = { donationSheetOpen = false }
        ) {
            DonationSheetLayout()
        }
    }
}

@Composable
private fun HomeScreenLayout(
    seasonalFeedState: SeasonalFeedState,
    followedMangaState: FollowedMangaState,
    popularFeedState: PopularFeedState,
    recentFeedState: RecentFeedState,
    refresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val loggedIn by rememberLoginState()

    HandleErrors2(
        seasonalFeedState,
        followedMangaState,
        popularFeedState,
        recentFeedState,
    )

    PullToRefreshScreen(
        modifier = Modifier,
        onRefresh = refresh,
        content = @Composable {
            ScrollableBox(
                modifier = modifier,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(mediumDp)
                ) {
                    FeatureCarousel(
                        modifier = Modifier,
                        mangaList = seasonalFeedState.manga,
                        isLoading = seasonalFeedState.loading,
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(mediumDp)
                    ) {
                        MangaShelf(
                            title = stringResource(id = R.string.home_screen_drawer_recently_popular),
                            list = popularFeedState.mangaList,
                            loading = popularFeedState.loading,
                            onTitleClicked = { navController.navigateToLibraryScreen(LibraryType.POPULAR) },
                        )

                        MangaShelf(
                            title = stringResource(id = R.string.home_page_drawer_recently_updated),
                            list = recentFeedState.list,
                            loading = recentFeedState.loading,
                        )

                        if (loggedIn) {
                            MangaShelf(
                                title = stringResource(id = R.string.library_screen_title),
                                list = followedMangaState.list,
                                loading = followedMangaState.loading,
                                onTitleClicked = {
                                    navController.navigateToLibraryScreen(
                                        LibraryType.FOLLOWS
                                    )
                                },
                            )
                        }

                        Spacer(modifier = Modifier)
                    }
                }
            }
        },
        seasonalFeedState,
        popularFeedState,
        if (rememberLoginState().value) followedMangaState else null,
        recentFeedState,
    )
}


@Composable
private fun HandleErrors(
    snackbarHostState: SnackbarHostState,
    onErrorMount: (UIError) -> Unit,
    vararg states: DataStoreState
) {
    states.mapNotNull { it.error }
        .forEach {
            OnMount {
                onErrorMount(it)
                snackbarHostState.showSnackbar(
                    it.getErrorTitle(),
                    duration = SnackbarDuration.Indefinite
                )
            }
        }
}

@Composable
private fun HandleErrors2(
    vararg states: DataStoreState,
) {
    val snackbarHostState = LocalSnackbarHostState.current

    states.map {
        LaunchedEffect(it.error) {
            val error = it.error
            if (error != null) {
                snackbarHostState.showSnackbar(
                    message = error.getErrorTitle(),
                    actionLabel = "Ok",
                    duration = SnackbarDuration.Indefinite
                )
            }
        }
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScreen() {
    DefaultPreview {
        HomeScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShort() {
    DefaultPreview {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            popularFeedState = PopularFeedState(
                mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            seasonalFeedState = SeasonalFeedState(
                manga = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
                name = "Season [Year]"
            ),
            recentFeedState = RecentFeedState(
                list = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            refresh = {},
        )
    }
}

@Preview(heightDp = 2000, showBackground = true)
@Composable
private fun Preview1() {
    DefaultPreview {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            popularFeedState = PopularFeedState(
                mangaList = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            seasonalFeedState = SeasonalFeedState(
                manga = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
                name = "Season [Year]"
            ),
            recentFeedState = RecentFeedState(
                list = StubData.Data.MANGA_LIST.toMangaList(),
                loading = false,
            ),
            refresh = {}
        )
    }
}