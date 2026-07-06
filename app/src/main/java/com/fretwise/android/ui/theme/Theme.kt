package com.fretwise.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FretiumColorScheme = darkColorScheme(
    primary = FretiumAccent,
    secondary = FretiumAccent2,
    background = FretiumBackground,
    surface = FretiumSurface,
    surfaceVariant = FretiumSurface2,
    outline = FretiumBorder,
    onBackground = FretiumText,
    onSurface = FretiumText,
    onPrimary = Color.White,
)

@Composable
fun FretiumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FretiumColorScheme,
        typography = FretiumTypography,
        content = content,
    )
}
