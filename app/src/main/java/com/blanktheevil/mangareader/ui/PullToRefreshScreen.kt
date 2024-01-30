package com.blanktheevil.mangareader.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.data.stores.DataStoreState
import com.blanktheevil.mangareader.ui.components.pullrefresh.PullRefreshIndicator
import com.blanktheevil.mangareader.ui.components.pullrefresh.pullRefresh
import com.blanktheevil.mangareader.ui.components.pullrefresh.rememberPullRefreshState

@Composable
fun PullToRefreshScreen(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
    vararg dataStoreStates: DataStoreState?,
) {
    val refreshState = rememberPullRefreshState(
        refreshing = dataStoreStates
            .filterNotNull()
            .any { it.loading },
        onRefresh = onRefresh,
    )

    Box(
        modifier = modifier.pullRefresh(refreshState)
    ) {
        content()

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = dataStoreStates.filterNotNull().any { it.loading },
            state = refreshState,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}