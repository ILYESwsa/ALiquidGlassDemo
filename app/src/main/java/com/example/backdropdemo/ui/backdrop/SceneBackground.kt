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
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop

/**
 * The single "source of truth" background that all glass surfaces in this
 * demo refract.
 *
 * IMPORTANT — this composable renders ONLY the background decoration
 * (gradient + [DecorativeBackground]) and nothing else. It must NOT contain
 * any content that itself refracts [backdrop] (e.g. a `GlassSurface`,
 * `GlassBottomTabs`, or the `NavHost` full of screens that use them).
 *
 * If a glass surface refracting `backdrop` were placed *inside* this
 * function's captured layer, the layer would need to render itself in order
 * to know what to render — Compose/Skia resolves that by recursing
 * (`SkiaDisplayList::prepareListAndChildren` calling itself) until the
 * native call stack overflows and the process crashes with a segfault. This
 * is the exact bug the library's own bottom-sheet tutorial warns about via
 * `exportedBackdrop` — the same rule applies here at the top level: whatever
 * calls `layerBackdrop(backdrop)` must be a sibling of, not a parent of,
 * anything that calls `drawBackdrop(backdrop)`.
 *
 * `Modifier.layerBackdrop(backdrop)` is what actually records these pixels
 * into the [backdrop] so downstream `drawBackdrop` calls elsewhere in the
 * tree (the NavHost's screens, the bottom bar) can sample them. This
 * modifier specifically requires a [LayerBackdrop] (the concrete type
 * `rememberLayerBackdrop()` returns) rather than the general `Backdrop`
 * interface, since it needs to know it's recording into a real Compose
 * graphics layer.
 */
@Composable
fun SceneBackground(
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Fill with a rich diagonal gradient FIRST, then capture it into
            // the backdrop via layerBackdrop.
            .background(
                Brush.linearGradient(
                    colors = listOf(GlassGradientStart, GlassGradientMid, GlassGradientEnd),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1400f)
                )
            )
            .layerBackdrop(backdrop)
    ) {
        DecorativeBackground()
    }
}
