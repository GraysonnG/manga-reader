package com.blanktheevil.mangareader.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blanktheevil.mangareader.OnMount
import com.blanktheevil.mangareader.PreviewDataFactory
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.domain.FollowedMangaState
import com.blanktheevil.mangareader.domain.PopularFeedState
import com.blanktheevil.mangareader.ui.components.HomeUserMenu
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBarState
import com.blanktheevil.mangareader.ui.components.MangaSearchBar
import com.blanktheevil.mangareader.ui.components.MangaShelf
import com.blanktheevil.mangareader.ui.components.pullrefresh.PullRefreshIndicator
import com.blanktheevil.mangareader.ui.components.pullrefresh.pullRefresh
import com.blanktheevil.mangareader.ui.components.pullrefresh.rememberPullRefreshState
import com.blanktheevil.mangareader.ui.sheets.DonationSheetLayout
import com.blanktheevil.mangareader.ui.sheets.SettingsSheetLayout
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    setTopAppBarState: (MangaReaderTopAppBarState) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToMangaDetail: (id: String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    val followedMangaState by homeViewModel.followedManga()
    val popularFeedState by homeViewModel.popularFeed()
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
        homeViewModel.initViewModel(context = context)
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            homeViewModel.searchManga(textInput)
        }
    }

    HomeScreenLayout(
        followedMangaState = followedMangaState,
        popularFeedState = popularFeedState,
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
    followedMangaState: FollowedMangaState,
    popularFeedState: PopularFeedState,
    searchText: String,
    searchMangaList: List<MangaDto>,
    refresh: () -> Unit,
    onTextChanged: (String) -> Unit,
    navigateToMangaDetail: (String) -> Unit,
    navigateToLibraryScreen: (LibraryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullRefreshState(
        refreshing = popularFeedState.loading,
        onRefresh = {
            refresh()
        }
    )
    val refreshing by remember { mutableStateOf(
        popularFeedState.loading || followedMangaState.loading
    ) }

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
        Box(
            modifier = Modifier.pullRefresh(refreshState),
        ) {
            Column(
                modifier = modifier
                    .padding(it),
            ) {
                Column(
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    val iconColor = if (isSystemInDarkTheme())
                        Color.White
                    else
                        MaterialTheme.colorScheme.primary


                    MangaSearchBar(
                        manga = searchMangaList,
                        value = searchText,
                        onValueChange = onTextChanged,
                        navigateToMangaDetail = navigateToMangaDetail,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                            focusedTrailingIconColor = iconColor,
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .verticalScroll(
                            state = rememberScrollState(),
                            enabled = true,
                        ),
                    verticalArrangement = Arrangement.spacedBy(72.dp)
                ) {
                    Spacer(modifier = Modifier)
                    MangaShelf(
                        title = stringResource(id = R.string.home_page_drawer_recently_popular),
                        list = popularFeedState.mangaList,
                        loading = popularFeedState.loading,
                        onCardClicked = navigateToMangaDetail,
                        onTitleClicked = { navigateToLibraryScreen(LibraryType.POPULAR) },
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

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = refreshing,
                state = refreshState,
                contentColor = MaterialTheme.colorScheme.primaryContainer,
            )
        }
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
            searchText = "",
            searchMangaList = emptyList(),
            onTextChanged = {},
            navigateToMangaDetail = {},
            navigateToLibraryScreen = {},
            refresh = {}
        )
    }
}