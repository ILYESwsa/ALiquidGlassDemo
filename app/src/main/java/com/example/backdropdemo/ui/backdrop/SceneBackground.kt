package com.example.backdropdemo.ui.backdrop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.backdropdemo.ui.theme.GlassGradientEnd
import com.example.backdropdemo.ui.theme.GlassGradientMid
import com.example.backdropdemo.ui.theme.GlassGradientStart
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop

/**
 * The single "source of truth" background that all glass surfaces in this
 * demo refract.
 *
 * IMPORTANT: for readable, believable glass, the *content behind the glass*
 * must have real visual variation (color/luminance) — a flat gray background
 * would make blur/lens/vibrancy invisible. This gradient plus [content]
 * (screen-specific decorative shapes) gives every glass surface something
 * meaningful to bend and frost.
 *
 * `Modifier.layerBackdrop(backdrop)` is what actually records these pixels
 * into the [backdrop] so downstream `drawBackdrop` calls elsewhere in the
 * tree can sample them.
 */
@Composable
fun SceneBackground(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Fill with a rich diagonal gradient FIRST, then capture it into
            // the backdrop via layerBackdrop — anything composed as
            // `content()` below is captured too, since layerBackdrop records
            // everything drawn within this layer, not just the background.
            .background(
                Brush.linearGradient(
                    colors = listOf(GlassGradientStart, GlassGradientMid, GlassGradientEnd),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1400f)
                )
            )
            .layerBackdrop(backdrop)
    ) {
        content()
    }
}
