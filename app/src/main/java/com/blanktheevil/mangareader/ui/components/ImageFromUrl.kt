package com.blanktheevil.mangareader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blanktheevil.mangareader.helpers.toAsyncPainterImage

@Composable
fun ImageFromUrl(
    modifier: Modifier = Modifier,
    url: String?,
    contentDescription: String? = null
) {
    val image = url.toAsyncPainterImage(
        crossfade = true
    )

    Image(
        modifier = modifier,
        painter = image,
        contentDescription = contentDescription,
    )
}