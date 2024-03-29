package com.blanktheevil.mangareader.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blanktheevil.mangareader.DefaultPreview
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.bottomBarVisible
import com.blanktheevil.mangareader.navigation.MangaReaderDestinations
import com.blanktheevil.mangareader.navigation.navigateToHistoryScreen
import com.blanktheevil.mangareader.navigation.navigateToHome
import com.blanktheevil.mangareader.navigation.navigateToListsScreen
import com.blanktheevil.mangareader.navigation.navigateToSearchScreen
import com.blanktheevil.mangareader.navigation.navigateToUpdatesScreen
import com.blanktheevil.mangareader.rememberLoginState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Theme

data class BottomBarItem(
    val label: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val route: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val authRequired: Boolean = false,
)

@Composable
fun MangaReaderBottomBar(
    modifier: Modifier,
    navController: NavController,
    imeState: Boolean,
    darkMode: String,
    theme: String,
) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val followIcon = painterResource(id = R.drawable.round_bookmark_border_24)

    val items = listOf(
        BottomBarItem(
            label = { Text(text = "Home") },
            icon = { Icon(imageVector = Icons.Rounded.Home, contentDescription = null) },
            route = MangaReaderDestinations.HOME(),
            onClick = navController::navigateToHome,
        ),
        BottomBarItem(
            label = { Text(text = "Search") },
            icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            route = MangaReaderDestinations.SEARCH(),
            onClick = navController::navigateToSearchScreen,
        ),
        BottomBarItem(
            label = { Text(text = "Updates") },
            icon = { Icon(followIcon, contentDescription = null) },
            route = MangaReaderDestinations.UPDATES(),
            onClick = navController::navigateToUpdatesScreen,
            authRequired = true
        ),
        BottomBarItem(
            label = { Text(text = "Lists") },
            icon = { Icon(imageVector = Icons.Rounded.List, contentDescription = null) },
            route = MangaReaderDestinations.LISTS(),
            onClick = navController::navigateToListsScreen,
            authRequired = true
        ),
        BottomBarItem(
            label = { Text(text = "History") },
            icon = {
                Icon(
                    imageVector = ImageVector
                        .vectorResource(id = R.drawable.baseline_history_24),
                    contentDescription = null
                )
            },
            route = MangaReaderDestinations.HISTORY(),
            onClick = navController::navigateToHistoryScreen,
        ),
    )

    AnimatedVisibility(
        visible = bottomBarVisible(),
        enter = expandVertically(
            expandFrom = Alignment.Bottom,
            animationSpec = tween()
        ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Bottom,
            animationSpec = tween()
        ),
    ) {
        val loggedIn by rememberLoginState()

        MangaReaderTheme(
            darkTheme = when (darkMode) {
                "system" -> isSystemInDarkTheme()
                "dark" -> true
                else -> false
            },
            theme = Theme.getFromSavedName(theme),
            dynamicColor = theme == "system"
        ) {
            NavigationBar(
                modifier = modifier,
            ) {
                items.forEach {
                    val selected = it.route == currentBackStackEntry?.destination?.route
                    val enabled = it.enabled && (!it.authRequired || loggedIn)

                    key(it.route) {
                        NavigationBarItem(
                            enabled = enabled,
                            icon = it.icon,
                            label = it.label,
                            selected = selected,
                            onClick = {
                                if (!selected) it.onClick()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(
    uiMode = UI_MODE_NIGHT_YES,
)
private fun Preview() {
    DefaultPreview {
        MangaReaderBottomBar(
            Modifier.fillMaxWidth(),
            navController = LocalNavController.current,
            imeState = false,
            darkMode = "dark",
            theme = "purple"
        )
    }
}

private data class BottomBarColors(
    val thing: String = ""
)