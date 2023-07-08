package com.blanktheevil.mangareader.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.blanktheevil.mangareader.R

// Set of Material typography styles to start with
private const val titleSize = 35
private val monaSans = FontFamily(
    Font(R.font.mona_sans_regular, FontWeight.Normal),
    Font(R.font.mona_sans_expanded_black, FontWeight.Bold),
)

private val Defaults = Typography()

val Typography = Typography(
    displayLarge = Defaults.displayLarge.copy(
        fontFamily = monaSans
    ),
    displayMedium = Defaults.displayMedium.copy(
        fontFamily = monaSans
    ),
    displaySmall = Defaults.displaySmall.copy(
        fontFamily = monaSans
    ),
    headlineLarge = Defaults.headlineLarge.copy(
        fontFamily = monaSans
    ),
    headlineMedium = Defaults.headlineMedium.copy(
        fontFamily = monaSans
    ),
    headlineSmall = Defaults.headlineSmall.copy(
        fontFamily = monaSans
    ),
    titleLarge = Defaults.titleLarge.copy(
        fontFamily = monaSans
    ),
    titleMedium = Defaults.titleMedium.copy(
        fontFamily = monaSans
    ),
    titleSmall = Defaults.titleSmall.copy(
        fontFamily = monaSans
    ),
    bodyLarge = Defaults.bodyLarge.copy(
        fontFamily = monaSans
    ),
    bodyMedium = Defaults.bodyMedium.copy(
        fontFamily = monaSans
    ),
    bodySmall = Defaults.bodySmall.copy(
        fontFamily = monaSans
    ),
    labelLarge = Defaults.labelLarge.copy(
        fontFamily = monaSans
    ),
    labelMedium = Defaults.labelMedium.copy(
        fontFamily = monaSans
    ),
    labelSmall = Defaults.labelSmall.copy(
        fontFamily = monaSans
    ),

)

val TypographyBold = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.mona_sans_expanded_black)
        ),
        fontSize = titleSize.sp,
        lineHeight = (titleSize * 1.2f).sp,
        letterSpacing = (-0.3).sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.knewave, FontWeight.Normal),
        ),
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
)