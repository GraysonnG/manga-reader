package com.blanktheevil.mangareader.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val xSmall = 4
val small = 8
val medium = 16
val large = 32
val xLarge = 64

val xSmallDp = xSmall.dp
val smallDp = small.dp
val mediumDp = medium.dp
val largeDp = large.dp
val xLargeDp = xLarge.dp

fun Modifier.xSmallPadding() = padding(xSmallDp)
fun Modifier.smallPadding() = padding(smallDp)
fun Modifier.mediumPadding() = padding(mediumDp)
fun Modifier.largePadding() = padding(largeDp)
fun Modifier.xLargePadding() = padding(xLargeDp)

fun Modifier.xSmallPaddingHorizontal() = padding(horizontal = xSmallDp)
fun Modifier.smallPaddingHorizontal() = padding(horizontal = smallDp)
fun Modifier.mediumPaddingHorizontal() = padding(horizontal = mediumDp)
fun Modifier.largePaddingHorizontal() = padding(horizontal = largeDp)
fun Modifier.xLargePaddingHorizontal() = padding(horizontal = xLargeDp)

fun Modifier.xSmallPaddingVertical() = padding(vertical = xSmallDp)
fun Modifier.smallPaddingVertical() = padding(vertical = smallDp)
fun Modifier.mediumPaddingVertical() = padding(vertical = mediumDp)
fun Modifier.largePaddingVertical() = padding(vertical = largeDp)
fun Modifier.xLargePaddingVertical() = padding(vertical = xLargeDp)


@Composable fun SpacerXSmall() = Spacer(modifier = Modifier.size(xSmallDp))
@Composable fun SpacerSmall() = Spacer(modifier = Modifier.size(smallDp))
@Composable fun SpacerMedium() = Spacer(modifier = Modifier.size(mediumDp))
@Composable fun SpacerLarge() = Spacer(modifier = Modifier.size(largeDp))
@Composable fun SpacerXLarge() = Spacer(modifier = Modifier.size(xLargeDp))

val RoundedCornerXSmall = RoundedCornerShape(xSmallDp)
val RoundedCornerSmall = RoundedCornerShape(smallDp)
val RoundedCornerMedium = RoundedCornerShape(mediumDp)
val RoundedCornerLarge = RoundedCornerShape(largeDp)
val RoundedCornerXLarge = RoundedCornerShape(xLargeDp)
