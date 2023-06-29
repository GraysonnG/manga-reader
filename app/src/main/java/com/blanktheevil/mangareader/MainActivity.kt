package com.blanktheevil.mangareader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.blanktheevil.mangareader.navigation.PrimaryNavGraph
import com.blanktheevil.mangareader.ui.theme.MangaReaderDefaults
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            val navController = rememberAnimatedNavController()
            var topAppBar: @Composable () -> Unit by remember { mutableStateOf({
                TopAppBar(title = {
                    Text(text = stringResource(id = R.string.app_name))
                }, colors = MangaReaderDefaults.topAppBarColors())
            }) }

            fun setTopAppBar(newTopAppBar: @Composable () -> Unit) {
                topAppBar = newTopAppBar
            }

            MangaReaderTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = topAppBar,
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

fun Activity.displayError(uiError: UIError) {
    (this as? MainActivity)?.displayError(uiError)
}
