package com.blanktheevil.mangareader

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.navigation.PrimaryNavGraph
import com.blanktheevil.mangareader.ui.RoundedCornerMedium
import com.blanktheevil.mangareader.ui.UIManager
import com.blanktheevil.mangareader.ui.components.MangaReaderBottomBar
import com.blanktheevil.mangareader.ui.components.MangaReaderScreen
import com.blanktheevil.mangareader.ui.components.MangaReaderTopAppBar
import com.blanktheevil.mangareader.ui.mediumDp
import com.blanktheevil.mangareader.ui.reader_v2.ReaderV2
import com.blanktheevil.mangareader.ui.rememberImeState
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Theme
import com.blanktheevil.mangareader.ui.xLargeDp
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private var settingsManager: SettingsManager? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        settingsManager = SettingsManager.getInstance().apply {
            init(this@MainActivity)
        }

        setContent {
            val navController = rememberNavController()
            val uiManager: UIManager = koinInject()
            var darkMode by remember { mutableStateOf(settingsManager!!.darkMode) }
            var theme by remember { mutableStateOf(settingsManager!!.theme) }
            val topAppBarState by uiManager.topAppBarState.collectAsState()
            val imeState by rememberImeState()
            val snackbarHostState = remember { SnackbarHostState() }

            OnMount {
                settingsManager?.addThemeChangedListener { newDarkMode, newTheme ->
                    darkMode = newDarkMode
                    theme = newTheme
                }
            }

            MangaReaderTheme(
                darkTheme = when (darkMode) {
                    "system" -> isSystemInDarkTheme()
                    "dark" -> true
                    else -> false
                },
                theme = Theme.getFromSavedName(theme),
                dynamicColor = theme == "system"
            ) {
                Box {
                    MangaReaderScreen(
                        snackbarHostState = snackbarHostState,
                        topBar = {
                            MangaReaderTopAppBar(
                                mangaReaderTopAppBarState = topAppBarState
                            )
                        },
                        bottomBar = {
                            CompositionLocalProvider(
                                LocalNavController provides navController,
                            ) {
                                MangaReaderBottomBar(
                                    modifier = Modifier,
                                    navController = navController,
                                    imeState = imeState,
                                    darkMode = darkMode,
                                    theme = theme,
                                )
                            }
                        },
                    ) {
                        CompositionLocalProvider(
                            LocalNavController provides navController,
                            LocalWindow provides window,
                            LocalSnackbarHostState provides snackbarHostState,
                        ) {
                            PrimaryNavGraph()
                        }
                    }

                    CompositionLocalProvider(
                        LocalNavController provides navController,
                        LocalWindow provides window,
                    ) {
                        ReaderV2()
                    }
                }
            }
        }
    }
}
