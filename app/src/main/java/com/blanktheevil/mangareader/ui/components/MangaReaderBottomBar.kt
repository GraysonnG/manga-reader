package com.blanktheevil.mangareader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.navigation.MangaReaderDestinations
import com.blanktheevil.mangareader.navigation.navigateToHome
import com.blanktheevil.mangareader.navigation.navigateToUpdatesScreen

@Composable
fun MangaReaderBottomBar(
    modifier: Modifier,
    navController: NavController
) {
    data class BottomBarItem(
        val label: @Composable () -> Unit,
        val icon: @Composable () -> Unit,
        val route: String,
        val onClick: () -> Unit,
    )
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
            label = { Text(text = "Updates") },
            icon = { Icon(followIcon, contentDescription = null) },
            route = MangaReaderDestinations.UPDATES(),
            onClick = navController::navigateToUpdatesScreen,
        ),
        BottomBarItem(
            label = { Text(text = "Lists") },
            icon = { Icon(imageVector = Icons.Rounded.List, contentDescription = null) },
            route = "null",
            onClick = { /*TODO*/ },
        ),
        BottomBarItem(
            label = { Text(text = "History") },
            icon = { Icon(imageVector = ImageVector
                .vectorResource(id = R.drawable.baseline_history_24), contentDescription = null) },
            route = "null",
            onClick = { /*TODO*/ },
        ),
    )
    val bottomBarVisible = currentBackStackEntry?.destination?.route in items.map { it.route }

    AnimatedVisibility(
        visible = bottomBarVisible,
        enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = tween()
                ) + fadeIn(
                    initialAlpha = 0.3f,
                    animationSpec = tween()
                ),
        exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = tween()
                ) + fadeOut(
                    animationSpec = tween()
                ),
    ) {
        NavigationBar(
            modifier = modifier,
        ) {
            items.forEach {
                val selected = it.route == currentBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = it.icon,
                    label = it.label,
                    selected = selected,
                    onClick = it.onClick,
                )
            }
        }
    }
}