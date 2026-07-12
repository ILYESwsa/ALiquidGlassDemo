package com.example.backdropdemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Liquid glass looks best over rich, colorful backgrounds because the backdrop
 * effects (blur, lens refraction, vibrancy) need real color/luminance variation
 * to be visible. A flat single-color background would make the glass invisible.
 */
val GlassGradientStart = Color(0xFF3A1C71)
val GlassGradientMid = Color(0xFFD76D77)
val GlassGradientEnd = Color(0xFFFFAF7B)

val AccentBlue = Color(0xFF0A84FF)
val AccentPink = Color(0xFFFF2D95)

private val LightColors = lightColorScheme(
    primary = AccentBlue,
    secondary = AccentPink,
    background = Color(0xFFF5F5F7),
    surface = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentPink,
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF121216)
)

@Composable
fun BackdropDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
