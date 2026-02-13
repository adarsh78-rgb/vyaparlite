package com.vyaparlite.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    secondary = BrandBlue,
    tertiary = BrandMint,
    background = SurfaceSoft,
    surface = CardSurface,
    onPrimary = CardSurface,
    onSecondary = CardSurface,
    onBackground = InkPrimary,
    onSurface = InkPrimary,
    error = Danger
)

@Composable
fun VyaparLiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
