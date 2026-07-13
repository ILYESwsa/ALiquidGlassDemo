package com.example.backdropdemo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

/**
 * ─────────────────────────────────────────────────────────────────────────
 * SECTION: "Glass Bottom Sheet"
 *
 * Demonstrates the library's `exportedBackdrop` parameter on `drawBackdrop`.
 *
 * The naive approach — recording the sheet's own content into a second
 * `layerBackdrop()` nested *inside* the glass surface that's drawing it —
 * creates a draw loop: the layer would need to draw itself in order to
 * capture itself. The library's actual solution is `exportedBackdrop`: pass
 * a second `LayerBackdrop` to `drawBackdrop`, and it will record the
 * glass surface's own rendered content (background refraction + children)
 * into that second backdrop *without* re-entering itself. Any other glass
 * element can then refract `sheetBackdrop` to "see" the sheet, exactly the
 * same way it would see the app's main scene.
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun GlassBottomSheetScreen(backdrop: Backdrop) {
    var sheetOpen by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Glass Bottom Sheet",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "The sheet exports its own rendered surface via\n" +
                    "exportedBackdrop, so other glass could refract it too.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { sheetOpen = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f))
            ) {
                Text("Open sheet", color = Color.White)
            }
        }

        AnimatedVisibility(
            visible = sheetOpen,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            GlassBottomSheetContent(
                sceneBackdrop = backdrop,
                onDismiss = { sheetOpen = false }
            )
        }

        // Scrim: dims the scene behind the sheet, without affecting the
        // backdrop capture itself (drawn as a plain overlay, not glass).
        if (sheetOpen) {
            val scrimInteractionSource = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(indication = null, interactionSource = scrimInteractionSource) {
                        sheetOpen = false
                    }
            )
        }
    }
}

@Composable
private fun GlassBottomSheetContent(
    sceneBackdrop: Backdrop,
    onDismiss: () -> Unit
) {
    // A second backdrop the sheet exports itself into — this is what other
    // glass elements would refract to "see" the sheet. We don't consume it
    // ourselves in this demo, but it's wired up to show the mechanism.
    val sheetBackdrop = rememberLayerBackdrop()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .drawBackdrop(
                backdrop = sceneBackdrop,
                exportedBackdrop = sheetBackdrop,
                shape = { RoundedCornerShape(32.dp) },
                effects = {
                    vibrancy()
                    blur(18.dp.toPx())
                    lens(refractionHeight = 24.dp.toPx(), refractionAmount = 32.dp.toPx(), chromaticAberration = true)
                },
                onDrawSurface = {
                    drawRect(Color.White.copy(alpha = 0.22f))
                }
            )
            .padding(24.dp)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Sheet title",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "This panel refracts the app scene behind it, and exports its " +
                "own rendered surface into a second backdrop via " +
                "exportedBackdrop — any other glass element could refract " +
                "the sheet itself using that.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f))
        ) {
            Text("Close", color = Color.White)
        }
    }
}

