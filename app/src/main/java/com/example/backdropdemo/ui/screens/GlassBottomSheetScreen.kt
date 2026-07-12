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
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.rememberCombinedSceneBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop

/**
 * ─────────────────────────────────────────────────────────────────────────
 * SECTION: "Glass Bottom Sheet"
 *
 * Demonstrates `rememberCombinedBackdrop`: the sheet has its OWN layer
 * backdrop (`sheetBackdrop`) that records its handle + header area, and it's
 * combined with the shared scene `backdrop` so the sheet's glass body can
 * refract both the app scene behind it AND its own header content, as a
 * single continuous surface.
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
                "Uses rememberCombinedBackdrop to merge the scene with the\n" +
                    "sheet's own recorded layer.",
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
    // The sheet's own backdrop layer — captures the sheet's handle + header
    // so that glass drawn on the sheet body can also refract its own chrome,
    // not only the scene behind it.
    val sheetLayerBackdrop = rememberLayerBackdrop()
    // Merge scene + sheet layer into one combined backdrop.
    val combined = rememberCombinedSceneBackdrop(scene = sceneBackdrop, floating = sheetLayerBackdrop)

    GlassSurface(
        backdrop = combined,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        tint = Color.White.copy(alpha = 0.22f),
        blurRadius = 18.dp
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                // Record the sheet's own header/handle into its layer
                // backdrop so `combined` above can refract it too.
                .layerBackdrop(sheetLayerBackdrop)
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
                "This entire panel is one liquid-glass surface refracting " +
                    "both the screen behind it and its own drag-handle layer, " +
                    "combined via rememberCombinedBackdrop.",
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
}
