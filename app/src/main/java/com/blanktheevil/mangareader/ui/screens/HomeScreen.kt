package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.FollowedMangaState
import com.blanktheevil.mangareader.domain.PopularFeedState
import com.blanktheevil.mangareader.domain.RecentFeedState
import com.blanktheevil.mangareader.domain.SeasonalFeedState
import com.blanktheevil.mangareader.ui.PullToRefreshScreen
import com.blanktheevil.mangareader.ui.components.FeatureCarousel
import com.blanktheevil.mangareader.ui.components.HomeUserMenu
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.sheets.DonationSheetLayout
import com.blanktheevil.mangareader.ui.sheets.SettingsSheetLayout
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val seasonalFeedState by homeViewModel.seasonalFeed()
    val followedMangaState by homeViewModel.followedManga()
    val popularFeedState by homeViewModel.popularFeed()
    val recentFeedState by homeViewModel.recentFeed()
    val userDataState by homeViewModel.userData()
    val textInput by homeViewModel.textInput.collectAsState()
    var settingsSheetOpen by remember { mutableStateOf(false) }
    var donationSheetOpen by remember { mutableStateOf(false) }
    val tipIcon = painterResource(id = R.drawable.twotone_coffee_24)
    val settingsIcon = painterResource(id = R.drawable.twotone_settings_24)
    val homeIcon = painterResource(id = R.drawable.twotone_home_24)

    setTopAppBarState(
        MangaReaderTopAppBarState(
            title = "Home",
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
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
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
                        navigateToLogin()
                    }
                )
            },
        )
    )

    OnMount {
        homeViewModel.initViewModel()
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            homeViewModel.searchManga(textInput)
        }
    }

    HomeScreenLayout(
        seasonalFeedState = seasonalFeedState,
        followedMangaState = followedMangaState,
        popularFeedState = popularFeedState,
        recentFeedState = recentFeedState,
        searchText = uiState.searchText,
        searchMangaList = uiState.searchMangaList,
        refresh = homeViewModel::refresh,
        onTextChanged = homeViewModel::onTextChanged,
        navigateToMangaDetail = navigateToMangaDetail,
        navigateToLibraryScreen = navigateToLibraryScreen,
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
    searchText: String,
    searchMangaList: List<MangaDto>,
    refresh: () -> Unit,
    onTextChanged: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
//    val refreshState = rememberPullRefreshState(
//        refreshing = popularFeedState.loading,
//        onRefresh = {
//            refresh()
//        }
//    )
//    val refreshing by remember {
//        mutableStateOf(
//            popularFeedState.loading || followedMangaState.loading
//        )
//    }

    LaunchedEffect(popularFeedState.error) {
        if (popularFeedState.error != null) {
            snackbarHostState.showSnackbar(
                "",
                duration = SnackbarDuration.Indefinite
            )
        }
    }



    Scaffold(
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(12.dp),
                hostState = snackbarHostState
            ) {
                popularFeedState.error?.let {
                    MangaReaderDefaults.DefaultErrorSnackBar(
                        snackbarHostState = snackbarHostState,
                        error = it
                    )
                }
            }
        }
    ) {
        PullToRefreshScreen(
            modifier = Modifier,
            onRefresh = refresh,
            content = @Composable {
                Column(
                    modifier = modifier
                        .padding(it),
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(
                                state = rememberScrollState(),
                                enabled = true,
                            ),
                        verticalArrangement = Arrangement.spacedBy(72.dp)
                    ) {
                        FeatureCarousel(
                            modifier = Modifier,
                            title = {},
                            mangaList = seasonalFeedState.manga,
                            onItemClicked = navigateToMangaDetail,
                            isLoading = seasonalFeedState.loading,
                        )

                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(72.dp)
                        ) {
                            MangaShelf(
                                title = stringResource(id = R.string.home_page_drawer_recently_popular),
                                list = popularFeedState.mangaList,
                                loading = popularFeedState.loading,
                                onCardClicked = navigateToMangaDetail,
                                onTitleClicked = { navigateToLibraryScreen(LibraryType.POPULAR) },
                            )

                            MangaShelf(
                                title = stringResource(id = R.string.home_page_drawer_recently_updated),
                                list = recentFeedState.list,
                                loading = recentFeedState.loading,
                                onCardClicked = navigateToMangaDetail,
                            )

                            MangaShelf(
                                title = stringResource(id = R.string.library_screen_title),
                                list = followedMangaState.list,
                                onCardClicked = navigateToMangaDetail,
                                loading = followedMangaState.loading,
                                onTitleClicked = { navigateToLibraryScreen(LibraryType.FOLLOWS) },
                            )

                            Spacer(modifier = Modifier)
                        }
                    }
                }
            },
            seasonalFeedState,
            popularFeedState,
            followedMangaState,
            recentFeedState,
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShort() {
    MangaReaderTheme {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            popularFeedState = PopularFeedState(
                mangaList = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            seasonalFeedState = SeasonalFeedState(
                manga = PreviewDataFactory.MANGA_LIST,
                loading = false,
                name = "Season [Year]"
            ),
            recentFeedState = RecentFeedState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            searchText = "",
            searchMangaList = emptyList(),
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToLibraryScreen = {},
            refresh = {},
        )
    }
}

@Preview(heightDp = 2000, showBackground = true)
@Composable
private fun Preview1() {
    MangaReaderTheme {
        HomeScreenLayout(
            followedMangaState = FollowedMangaState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            popularFeedState = PopularFeedState(
                mangaList = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            seasonalFeedState = SeasonalFeedState(
                manga = PreviewDataFactory.MANGA_LIST,
                loading = false,
                name = "Season [Year]"
            ),
            recentFeedState = RecentFeedState(
                list = PreviewDataFactory.MANGA_LIST,
                loading = false,
            ),
            searchText = "",
            searchMangaList = emptyList(),
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToLibraryScreen = {},
            refresh = {}
        )
    }
}