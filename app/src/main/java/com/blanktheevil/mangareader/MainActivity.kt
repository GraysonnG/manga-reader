package com.blanktheevil.mangareader

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.blanktheevil.mangareader.data.settings.SettingsManager
import com.blanktheevil.mangareader.navigation.PrimaryNavGraph
import com.blanktheevil.mangareader.ui.components.MangaReaderBottomBar
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.blanktheevil.mangareader.ui.theme.Theme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    private var settingsManager: SettingsManager? = null

    @SuppressLint("SourceLockedOrientationActivity")
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        settingsManager = SettingsManager.getInstance().apply {
            init(this@MainActivity)
        }

        setContent {
            val navController = rememberAnimatedNavController()
            var topAppBar: @Composable () -> Unit by remember { mutableStateOf({}) }
            var darkMode by remember { mutableStateOf(settingsManager!!.darkMode) }
            var theme by remember { mutableStateOf(settingsManager!!.theme) }

            fun setTopAppBar(newTopAppBar: @Composable () -> Unit) {
                topAppBar = newTopAppBar
            }

            OnMount {
                settingsManager?.addThemeChangedListener { newDarkMode, newTheme ->
                    darkMode = newDarkMode
                    theme = newTheme
                }
            }

            MangaReaderTheme(
                darkTheme = when(darkMode) {
                    "system" -> isSystemInDarkTheme()
                    "dark" -> true
                    else -> false
                },
                theme = Theme.getFromSavedName(theme),
                dynamicColor = theme == "system"
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = topAppBar,
                        bottomBar = {
                            MangaReaderBottomBar(modifier = Modifier, navController = navController)
                        }
                    ) {
                        PrimaryNavGraph(
                            modifier = Modifier.padding(it),
                            navController = navController,
                            setTopAppBar = ::setTopAppBar
                        )
                    }
                }
            }
        }
    }
}
