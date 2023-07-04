package com.blanktheevil.mangareader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val Purple40 = Color(0xFFA096FF)
val Purple20 = Purple40.lighten(0.5f)
val Purple80 = Purple40.darken(0.5f)
val GREEN_50 = Color(0xff96ffa0)
val ERROR_50 = Color(0xffff7d7d)

val Md_Primary50 = Color(0xffff6740)
val Md_Primary20 = Md_Primary50.lighten(0.5f)
val Md_Primary10 = Md_Primary50.lighten(0.33f)
val Md_Primary80 = Md_Primary50.darken(0.5f)
val Md_Primary60 = Md_Primary50.darken(0.33f)
val Md_Green50 = Color(0xff04d000)
val MD_Error50 = Color(0xffe74c3c)

fun Color.darken(darkness: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        this.toArgb(),
        hsv
    )
    hsv[2] *= darkness
    return Color.hsv(hsv[0], hsv[1], hsv[2])
}

fun Color.lighten(lightness: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        this.toArgb(),
        hsv
    )
    hsv[2] += (1f - hsv[2]) * lightness
    hsv[1] *= lightness
    return Color.hsv(hsv[0], hsv[1], hsv[2])
}