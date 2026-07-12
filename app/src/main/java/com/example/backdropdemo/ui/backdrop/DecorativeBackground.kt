package com.example.backdropdemo.ui.backdrop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Floating translucent circles drawn on top of the base gradient (but still
 * *behind* the glass surfaces, and still inside the recorded backdrop layer).
 *
 * These exist purely so `lens()` and `blur()` have distinct shapes/edges to
 * refract — without them, a smooth gradient alone shows the effect but big
 * circular "blobs" make the liquid-glass bending much easier to see and
 * demonstrate, especially chromatic aberration at the edges.
 */
@Composable
fun DecorativeBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = w * 0.35f,
            center = Offset(w * 0.15f, h * 0.2f)
        )
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.22f),
            radius = w * 0.28f,
            center = Offset(w * 0.85f, h * 0.35f)
        )
        drawCircle(
            color = Color(0xFFFFEA00).copy(alpha = 0.16f),
            radius = w * 0.3f,
            center = Offset(w * 0.3f, h * 0.75f)
        )
        drawCircle(
            color = Color(0xFFFF3D81).copy(alpha = 0.18f),
            radius = w * 0.25f,
            center = Offset(w * 0.9f, h * 0.85f)
        )
    }
}
