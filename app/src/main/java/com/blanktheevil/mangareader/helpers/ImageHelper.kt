package com.blanktheevil.mangareader.helpers

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Composable
fun String?.toAsyncPainterImage(
    crossfade: Boolean = false,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): AsyncImagePainter {
    val context = LocalContext.current
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .dispatcher(dispatcher)
            .data(this)
            .crossfade(crossfade)
            .scale(Scale.FIT)
            .build()
    )
}