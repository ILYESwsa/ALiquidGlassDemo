package com.example.backdropdemo.ui.backdrop

/*
 * ─────────────────────────────────────────────────────────────────────────
 * GLASS COMPONENTS — reusable building blocks on top of the CMP Backdrop
 * library (io.github.kyant0:backdrop-android).
 *
 * MENTAL MODEL OF THE LIBRARY
 * ----------------------------
 * 1. A `Backdrop` is a *recording* of whatever pixels were drawn behind it.
 *    You create one with `rememberLayerBackdrop()` (records a Compose layer),
 *    `rememberCanvasBackdrop()` (records a raw Canvas you draw into
 *    yourself), `rememberCombinedBackdrop()` (merges several backdrops into
 *    one, e.g. content + a floating layer), or you fall back to
 *    `emptyBackdrop()` when no backdrop is available yet (safe default /
 *    placeholder so composables never need a nullable Backdrop).
 *
 * 2. You attach a backdrop to the layer that should be "captured" using
 *    `Modifier.layerBackdrop(backdrop)`. Anything drawn under this modifier
 *    becomes the source pixels that glass surfaces elsewhere can refract,
 *    blur and tint. IMPORTANT: whatever calls `layerBackdrop(backdrop)` must
 *    be a SIBLING of, never a PARENT of, anything that calls
 *    `drawBackdrop(backdrop)` — nesting them causes the render tree to need
 *    to draw itself to know what to draw, which crashes with a native stack
 *    overflow. See SceneBackground.kt's doc comment for the full story.
 *
 * 3. Any composable that should *look like glass* uses
 *    `Modifier.drawBackdrop(backdrop, shape, effects, onDrawSurface)`:
 *      - `shape`      → the outline of the glass (rounded rect, circle...)
 *      - `effects`    → a DSL where you chain `vibrancy()`, `blur()`,
 *                        `lens()` — these sample the recorded backdrop and
 *                        distort/blur/saturate it, exactly like real glass.
 *      - `onDrawSurface` → drawn *after* the effects, this is where you add
 *                        the translucent tint / highlight that makes the
 *                        glass visible and keeps text readable on top of it.
 *
 * The more elaborate interactive components (GlassButton, GlassToggle,
 * GlassBottomTabs, GlassSlider) live in their own files, each ported closely
 * from the library's own official catalog app so the drag/press physics
 * match the real thing rather than a simplified approximation.
 * ─────────────────────────────────────────────────────────────────────────
 */

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

/**
 * Convenience wrapper for the app's single top-level scene backdrop.
 *
 * The library exposes `rememberBackdrop` as its general umbrella API for
 * creating a `Backdrop`; `rememberLayerBackdrop()` is the concrete,
 * layer-capturing implementation of it that this demo uses everywhere a
 * Compose layer (rather than a hand-drawn Canvas or a combination of other
 * backdrops) needs to be recorded — which is the case for the main scene.
 */
@Composable
fun rememberSceneBackdrop(): LayerBackdrop = rememberLayerBackdrop()

/**
 * Draws a rounded-rect / circle "pane" of liquid glass over [backdrop].
 *
 * This is the simplest glass building block in the demo — a static panel
 * with no press/drag physics — used for cards, sheets, and section
 * backgrounds. For interactive controls, see GlassButton, GlassToggle,
 * GlassBottomTabs, and GlassSlider instead.
 *
 * @param backdrop        the recorded background this glass will refract.
 * @param shape           outline of the glass pane.
 * @param tint            translucent color drawn on top of the refracted
 *                         backdrop — this is what gives the glass its subtle
 *                         color cast and keeps foreground content readable.
 * @param blurRadius      how much the backdrop is blurred before being shown
 *                         through the glass (frosted-glass strength).
 * @param refractionHeight / refractionAmount → control the "lens" bulge:
 *                         how tall the refraction band is near the edges and
 *                         how strongly it bends the pixels underneath it,
 *                         mimicking a curved glass edge.
 * @param chromaticAberration adds a tiny RGB split at the refraction edge,
 *                         the detail that sells "real glass" over "blur".
 * @param border          optional 1dp hairline to catch the eye like a real
 *                         glass edge highlight.
 */
@Composable
fun GlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    tint: Color = Color.White.copy(alpha = 0.16f),
    blurRadius: Dp = 12.dp,
    refractionHeight: Dp = 24.dp,
    refractionAmount: Dp = 32.dp,
    chromaticAberration: Boolean = true,
    border: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    // vibrancy() boosts saturation/contrast of the sampled
                    // backdrop pixels so colors "pop" through the glass,
                    // the same way frosted glass intensifies light behind it.
                    vibrancy()
                    // blur() frosts the backdrop — bigger radius = thicker,
                    // more opaque-looking glass.
                    blur(blurRadius.value)
                    // lens() bends backdrop pixels near the shape's edge to
                    // simulate the curved surface of real glass; chromatic
                    // aberration adds the subtle color fringing real lenses
                    // produce at high-contrast edges.
                    lens(
                        refractionHeight = refractionHeight.value,
                        refractionAmount = refractionAmount.value,
                        chromaticAberration = chromaticAberration
                    )
                },
                onDrawSurface = {
                    // Drawn AFTER the effects above: this is the translucent
                    // "surface" tint. Without this, refracted/blurred pixels
                    // alone can look muddy and foreground text becomes hard
                    // to read — the tint restores contrast and gives the
                    // glass its visible color identity.
                    drawRect(tint)
                }
            )
            .let {
                if (border) {
                    it.border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.35f),
                        shape = shape
                    )
                } else it
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Demonstrates `rememberCombinedBackdrop`: merges the *scene* backdrop
 * (the app's main gradient/content layer) with a *floating* backdrop (e.g. a
 * bottom sheet's own drag handle layer) into a single Backdrop. Any glass
 * surface drawn against the combined backdrop refracts pixels from BOTH
 * sources as if they were one continuous background — useful when a floating
 * glass element should also refract other floating glass elements above the
 * base scene, not just the scene itself.
 *
 * GlassToggle, GlassBottomTabs, and GlassSlider all use
 * `rememberCombinedBackdrop` directly and more elaborately (combining the
 * scene with a *transformed* view of a sibling's own backdrop); this
 * standalone wrapper demonstrates the simpler two-backdrop case on its own.
 */
@Composable
fun rememberCombinedSceneBackdrop(scene: Backdrop, floating: Backdrop): Backdrop =
    rememberCombinedBackdrop(scene, floating)

/**
 * Demonstrates `rememberCanvasBackdrop`: a backdrop you populate yourself by
 * drawing directly into a Canvas, instead of capturing a Compose layer. Used
 * here to build a small procedural "swatch" backdrop — a colorful gradient
 * painted with normal DrawScope calls that a glass surface can then refract,
 * exactly like it would refract real app content.
 */
@Composable
fun rememberProceduralBackdrop(colors: List<Color>): Backdrop =
    rememberCanvasBackdrop {
        // Inside this DrawScope, `size` is the backdrop's own draw area. We
        // paint a diagonal gradient sweep so blur/lens has clear color
        // transitions to bend and frost — flat colors would make the glass
        // effects invisible.
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
    }

/**
 * Demonstrates `emptyBackdrop()`: a safe no-op Backdrop used as a fallback
 * default parameter value, so composables that *optionally* render glass
 * never need a nullable `Backdrop?` type. When a real backdrop hasn't been
 * created yet (e.g. during the first composition frame), effects simply
 * sample nothing and the surface tint alone is shown.
 */
@Composable
fun rememberFallbackBackdrop(): Backdrop = emptyBackdrop()
