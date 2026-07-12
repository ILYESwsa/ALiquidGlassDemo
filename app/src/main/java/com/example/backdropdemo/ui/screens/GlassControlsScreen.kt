package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.rememberProceduralBackdrop
import com.kyant.backdrop.Backdrop
import kotlin.math.roundToInt

/**
 * ─────────────────────────────────────────────────────────────────────────
 * SECTION: "Glass Slider"
 *
 * Demonstrates `rememberCanvasBackdrop`: instead of capturing a Compose
 * layer, we procedurally paint a small gradient "swatch" backdrop and let
 * the slider's glass thumb refract THAT, independent of the app scene. This
 * shows the library isn't limited to capturing existing UI — any Canvas
 * content can act as a backdrop source.
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun GlassControlsScreen(backdrop: Backdrop) {
    Column(
        Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(20.dp, 32.dp, 20.dp, 140.dp)
    ) {
        Text(
            "Glass Slider",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Thumb refracts a procedural rememberCanvasBackdrop swatch, " +
                "independent of the app scene behind it.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(Modifier.height(40.dp))
        GlassSliderDemo()

        Spacer(Modifier.height(48.dp))
        Text(
            "Glass track over app scene",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        // A second slider whose TRACK is glass over the real app scene
        // backdrop, contrasting with the thumb's own procedural backdrop.
        GlassSurface(
            backdrop = backdrop,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            tint = Color.White.copy(alpha = 0.16f),
            blurRadius = 10.dp
        ) {
            Text(
                "Scene-backed glass track",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GlassSliderDemo() {
    var progress by remember { mutableFloatStateOf(0.35f) }

    // A small procedural backdrop — an animated-feeling color sweep painted
    // directly via rememberCanvasBackdrop, used ONLY by the thumb, so the
    // thumb's glass always has rich color to refract regardless of what part
    // of the track it sits over.
    val thumbBackdrop = rememberProceduralBackdrop(
        colors = listOf(
            Color(0xFF00E5FF),
            Color(0xFFFF3D81),
            Color(0xFFFFEA00)
        )
    )

    BoxWithConstraints(Modifier.fillMaxWidth().height(56.dp)) {
        val density = LocalDensity.current
        val trackWidthPx = constraints.maxWidth.toFloat()
        val thumbSizePx = with(density) { 48.dp.toPx() }
        val usableWidth = trackWidthPx - thumbSizePx

        // Track background — plain translucent capsule (not glass) so the
        // glass thumb reads clearly as the "special" element.
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(8.dp)
                .padding(horizontal = 4.dp)
                .background(Color.White.copy(alpha = 0.25f), CircleShape)
        )

        val thumbOffsetX = with(density) { (usableWidth * progress).toDp() }

        GlassSurface(
            backdrop = thumbBackdrop,
            modifier = Modifier
                .offset(x = thumbOffsetX)
                .size(48.dp)
                .pointerInput(usableWidth) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val next = (progress * usableWidth + dragAmount) / usableWidth
                        progress = next.coerceIn(0f, 1f)
                    }
                },
            shape = CircleShape,
            tint = Color.White.copy(alpha = 0.3f),
            blurRadius = 8.dp,
            refractionHeight = 18.dp,
            refractionAmount = 24.dp
        ) {
            Text(
                "${(progress * 100).roundToInt()}",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
