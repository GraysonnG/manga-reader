package com.blanktheevil.mangareader.ui.reader_v2

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blanktheevil.mangareader.LocalNavController
import com.blanktheevil.mangareader.R
import com.blanktheevil.mangareader.bottomBarVisible
import com.blanktheevil.mangareader.data.ReaderType
import com.blanktheevil.mangareader.data.reader.ReaderManager
import com.blanktheevil.mangareader.data.reader.ReaderManagerState
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage
import com.blanktheevil.mangareader.navigation.navigateToMangaDetailScreen
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.smallDp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.math.max

@Composable
fun ReaderV2() {
    val readerManager = koinInject<ReaderManager>()
    val readerState by readerManager.state.collectAsState()
    val readerMiniSize by remember { mutableStateOf(100.dp) }
    val configuration = LocalConfiguration.current
    val systemUIController = rememberSystemUiController()
    val expanded = readerState.expanded
    val chapter = readerState.currentChapter
    val manga = readerState.manga
    val statusBarColor = MaterialTheme.colorScheme.secondaryContainer

    LaunchedEffect(expanded, chapter) {
        if (expanded && chapter != null) {
            systemUIController.setStatusBarColor(
                color = Color.Black,
            )
        } else {
            systemUIController.setStatusBarColor(
                color = statusBarColor
            )
        }
    }

    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 0.dp else mediumDp,
        animationSpec = tween(),
        label = "corner"
    )

    val width by animateDpAsState(
        targetValue = if (expanded) configuration.screenWidthDp.dp else readerMiniSize,
        animationSpec = tween(),
        label = "width"
    )

    val height by animateDpAsState(
        targetValue = if (expanded) configuration.screenHeightDp.dp else readerMiniSize.times(
            16 / 10f
        ),
        animationSpec = tween(),
        label = "height"
    )

    val yOffset by animateDpAsState(
        targetValue = when {
            expanded -> 0.dp
            !expanded && bottomBarVisible() -> 80.dp.unaryMinus()
            else -> 0.dp
        },
        animationSpec = tween(),
        "yOffset"
    )

    AnimatedVisibility(
        visible = readerState.currentChapter != null,
        enter = slideInVertically { -100 } + fadeIn(),
        exit = slideOutVertically { 100 } + fadeOut(),
    ) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                Modifier
                    .offset(y = yOffset)
                    .padding(if (expanded) 0.dp else mediumDp),
            ) {
                Surface(
                    modifier = Modifier
                        .width(width)
                        .height(height),
                    color = Color.Black,
                    shape = RoundedCornerShape(cornerRadius),
                    shadowElevation = if (expanded) 0.dp else 8.dp
                ) {
                    CompositionLocalProvider(value = LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        AnimatedVisibility(visible = !expanded) {
                            Box(Modifier.fillMaxSize()) {
                                MiniView(
                                    readerState = readerState,
                                    readerManager = readerManager,
                                    coverImageUrl = manga?.coverArt,
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = expanded,
                            exit = ExitTransition.None,
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                FullView(
                                    readerManager = readerManager,
                                    readerState = readerState,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.MiniView(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
    coverImageUrl: String?
) {
    val view = LocalView.current

    Image(
        modifier = Modifier.fillMaxSize(),
        painter = coverImageUrl.toAsyncPainterImage(
            crossfade = true
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.5f
    )

    IconButton(
        modifier = Modifier.align(Alignment.Center),
        onClick = { readerManager.expandReader() }
    ) {
        Icon(
            painterResource(id = R.drawable.baseline_fullscreen_24),
            contentDescription = null
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val chapterName = if (readerState.currentChapter?.chapter != null)
            "Ch. ${readerState.currentChapter.chapter}"
        else
            "Ch. ..."

        Text(
            modifier = Modifier.padding(start = smallDp),
            text = chapterName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )

        IconButton(
            onClick = {
                readerManager.closeReader()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_close_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BoxScope.FullView(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
) {
    var uiVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiVisible) {
        if (uiVisible) {
            delay(3000)
            uiVisible = false
        }
    }

    LaunchedEffect(readerState.currentChapterLoading) {
        if (readerState.currentChapterLoading) {
            uiVisible = true
        }
    }

    BackHandler {
        readerManager.shrinkReader()
    }

    if (readerState.currentChapterLoading) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    } else {
        when (readerState.readerType) {
            ReaderType.PAGE -> {
                PageReader(
                    currentPage = readerState.currentPage,
                    maxPages = readerState.currentChapterPageUrls.size,
                    pageUrls = readerState.currentChapterPageUrls,
                    nextButtonClicked = readerManager::nextPage,
                    prevButtonClicked = readerManager::prevPage,
                    middleButtonClicked = {
                        uiVisible = !uiVisible
                    },
                )
            }

            ReaderType.VERTICAL -> {
                StripReader(
                    pageUrls = readerState.currentChapterPageUrls,
                    onScreenClick = { uiVisible = !uiVisible },
                    nextButtonClicked = readerManager::nextChapter,
                    onLastPageViewed = readerManager::markChapterRead,
                )
            }

            ReaderType.HORIZONTAL -> {
                StripReader(
                    isVertical = false,
                    pageUrls = readerState.currentChapterPageUrls,
                    onScreenClick = { uiVisible = !uiVisible },
                    nextButtonClicked = readerManager::nextChapter,
                    onLastPageViewed = readerManager::markChapterRead,
                )
            }
        }

        ProgressBar(
            readerState = readerState
        )

        AnimatedVisibility(
            visible = uiVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                PageUI(
                    readerManager = readerManager,
                    readerState = readerState,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.PageUI(
    readerManager: ReaderManager,
    readerState: ReaderManagerState,
) {
    val navController = LocalNavController.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .background(Color.Black.copy(0.8f))
            .fillMaxWidth()
            .align(Alignment.TopCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { readerManager.toggleReader() }
        ) {
            Icon(
                painterResource(id = R.drawable.round_keyboard_arrow_down_24),
                contentDescription = null
            )
        }

        readerState.manga?.let {
            Text(
                text = it.title.trim(),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        role = Role.Button
                    ) {
                        readerState.mangaId?.let {
                            coroutineScope.launch {
                                navController.navigateToMangaDetailScreen(it)
                                delay(100)
                                readerManager.shrinkReader()
                            }
                        }
                    },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        IconButton(
            onClick = {
                readerManager.closeReader()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                }
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_close_24),
                contentDescription = null
            )
        }
    }

    Row(
        modifier = Modifier
            .background(Color.Black.copy(0.8f))
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = readerManager::prevChapter) {
            Icon(
                painterResource(R.drawable.round_chevron_left_24),
                contentDescription = null
            )
        }

        readerState.currentChapter?.let {
            Text(
                text = it.shortTitle.trim(),
                modifier = Modifier.weight(1f, fill = true),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        IconButton(onClick = readerManager::nextChapter) {
            Icon(
                painterResource(R.drawable.round_chevron_right_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun BoxScope.ProgressBar(
    readerState: ReaderManagerState,
) {
    val progress =
        readerState.currentPage
            .toFloat()
            .plus(1f) / max(1f, readerState.currentChapterPageUrls.size.toFloat())

    val progressAnim by animateFloatAsState(targetValue = progress, label = "progress")

    Row(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        readerState.currentChapterPageLoaded.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (it)
                            Color.White.copy(alpha = 0.3f)
                        else
                            Color.White.copy(alpha = 0.1f)
                    )
            )
        }
    }

    LinearProgressIndicator(
        progress = progressAnim,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart),
        color = MaterialTheme.colorScheme.primary,
        trackColor = Color.Transparent,
    )
}