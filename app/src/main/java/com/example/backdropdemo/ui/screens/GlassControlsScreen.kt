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
import com.example.backdropdemo.ui.backdrop.GlassSlider
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.rememberProceduralBackdrop
import com.kyant.backdrop.Backdrop
import kotlin.math.roundToInt

/**
 * ─────────────────────────────────────────────────────────────────────────
 * SECTION: "Glass Slider"
 *
 * `GlassSlider` is a full port of the library's own LiquidSlider: tap the
 * track to jump there, or drag the thumb directly. The track is its own
 * independent backdrop layer, and the thumb refracts a COMBINED backdrop of
 * the app scene plus a squash/stretch-transformed view of the track's own
 * pixels — press and hold the thumb to see it compress.
 *
 * Below that, a second, simpler slider demonstrates `rememberCanvasBackdrop`
 * directly: instead of capturing a Compose layer, a small gradient "swatch"
 * is painted by hand, and the thumb refracts THAT — independent of the app
 * scene — showing the library isn't limited to capturing existing UI.
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun GlassControlsScreen(backdrop: Backdrop) {
    var sliderValue by remember { mutableFloatStateOf(0.4f) }

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
            "Tap the track to jump, or drag the thumb — hold it to feel it compress.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(Modifier.height(32.dp))
        GlassSlider(
            value = { sliderValue },
            onValueChange = { sliderValue = it },
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            backdrop = backdrop,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${(sliderValue * 100).roundToInt()}%",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(48.dp))
        Text(
            "Procedural backdrop slider",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Thumb refracts a hand-painted rememberCanvasBackdrop swatch, " +
                "independent of the app scene behind it.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(Modifier.height(24.dp))
        ProceduralSliderDemo()
    }
}

@Composable
private fun ProceduralSliderDemo() {
    var progress by remember { mutableFloatStateOf(0.35f) }

    // A small procedural backdrop — a color sweep painted directly via
    // rememberCanvasBackdrop, used ONLY by the thumb, so the thumb's glass
    // always has rich color to refract regardless of what part of the track
    // it sits over.
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
