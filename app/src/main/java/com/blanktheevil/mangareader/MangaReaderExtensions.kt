package com.blanktheevil.mangareader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.blanktheevil.mangareader.data.dto.ChapterDto
import com.blanktheevil.mangareader.data.dto.MangaDto
import com.blanktheevil.mangareader.di.appModule
import com.blanktheevil.mangareader.di.dataStoresModule
import com.blanktheevil.mangareader.ui.theme.MangaReaderTheme
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@Composable
fun OnMount(block: suspend CoroutineScope.() -> Unit) =
    LaunchedEffect(Unit, block = block)

fun <T, U> letIfNotNull(
    arg1: T?,
    arg2: U?,
    block: (T, U) -> Unit,
) {
    arg1?.let { t ->
        arg2?.let { u ->
            block(t, u)
        }
    }
}

val LocalNavController =
    compositionLocalOf<NavHostController> { error("No NavController") }

fun <T, U, V> letIfNotNull(
    arg1: T?,
    arg2: U?,
    arg3: V?,
    block: (T, U, V) -> Unit,
) {
    arg1?.let { t ->
        arg2?.let { u ->
            arg3?.let { v ->
                block(t, u, v)
            }
        }
    }
}

@Composable
fun DefaultPreview(block: @Composable () -> Unit) {
    val context = LocalContext.current


    if (GlobalContext.getOrNull() == null) {
        startKoin {
            androidContext(context)

            modules(
                appModule,
                dataStoresModule,
            )
        }
    }

    MangaReaderTheme {
        CompositionLocalProvider(
            LocalNavController provides rememberNavController(),
            content = block
        )
    }
}

typealias MangaList = List<MangaDto>
typealias ChapterList = List<ChapterDto>