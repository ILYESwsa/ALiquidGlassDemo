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
 *    blur and tint.
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
 * ─────────────────────────────────────────────────────────────────────────
 */

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
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
import kotlinx.coroutines.launch

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
 * This is the single most reused piece of the demo: bottom bars, bottom
 * sheets, sliders and buttons all funnel through this composable so every
 * glass surface in the app is visually consistent.
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
    content: @Composable Box.() -> Unit = {}
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
 * A tinted, "liquid glass" icon-button surface.
 *
 * Demonstrates `BlendMode.Hue`: instead of simply overlaying a flat colored
 * rectangle (which would just dim/muddy the backdrop), we blend only the
 * *hue* channel of [tint] onto the refracted backdrop pixels, keeping their
 * original luminance/saturation. The result: the glass still looks like
 * glass (you can see light and dark variation through it) but is unmistakably
 * colored, exactly like tinted automotive or architectural glass.
 */
@Composable
fun TintedGlassIconButton(
    backdrop: Backdrop,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    vibrancy()
                    blur(6f)
                    lens(refractionHeight = 14f, refractionAmount = 18f, chromaticAberration = true)
                },
                onDrawSurface = {
                    // Base translucent glass tint so the button reads as a
                    // surface even before the hue tint is applied.
                    drawRect(Color.White.copy(alpha = 0.14f))
                    // BlendMode.Hue: paints only the tint's hue over the
                    // refracted pixels beneath, preserving their brightness.
                    // This is what lets colored glass still show highlights
                    // and shadows from whatever is behind it.
                    drawRect(
                        color = tint.copy(alpha = 0.85f),
                        blendMode = BlendMode.Hue
                    )
                }
            )
            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

/**
 * Interactive glass "pill" used inside the bottom bar. Demonstrates the
 * documented `layerBlock` pattern: on press, the pill scales up, but the
 * scale is applied through `drawBackdrop`'s own `layerBlock` (not a separate
 * `graphicsLayer`) so the *backdrop sampling itself* doesn't scale — only the
 * visible glass shape does. Getting this wrong makes the refraction sample
 * area drift away from the visual bounds when pressed.
 */
@Composable
fun InteractiveGlassPill(
    backdrop: Backdrop,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable Box.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val pressProgress = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    vibrancy()
                    blur(4f)
                    lens(refractionHeight = 16f, refractionAmount = 32f, chromaticAberration = true)
                },
                // layerBlock scales the *rendered glass layer*, independent
                // from the backdrop capture coordinates, so refraction stays
                // pixel-accurate while the pill visually grows on press.
                layerBlock = {
                    val progress = pressProgress.value
                    val maxScale = (size.width + 16f.dp.toPx()) / size.width
                    val scale = lerp(1f, maxScale, progress)
                    scaleX = scale
                    scaleY = scale
                },
                onDrawSurface = {
                    val alpha = if (selected) 0.55f else 0.32f
                    drawRect(Color.White.copy(alpha = alpha))
                }
            )
            .clickable(interactionSource = null, indication = null, onClick = onClick)
            .pointerInput(scope) {
                val spec = spring<Float>(dampingRatio = 0.5f, stiffness = 300f)
                awaitEachGesture {
                    awaitFirstDown()
                    scope.launch { pressProgress.animateTo(1f, spec) }
                    waitForUpOrCancellation()
                    scope.launch { pressProgress.animateTo(0f, spec) }
                }
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
 */
@Composable
fun rememberCombinedSceneBackdrop(scene: Backdrop, floating: Backdrop): Backdrop =
    rememberCombinedBackdrop(scene, floating)

/**
 * Demonstrates `rememberCanvasBackdrop`: a backdrop you populate yourself by
 * drawing directly into a Canvas, instead of capturing a Compose layer. Used
 * here to build a small procedural "swatch" backdrop for the glass slider —
 * a colorful animated gradient painted with normal DrawScope calls that the
 * slider's glass thumb can then refract, exactly like it would refract real
 * app content.
 */
@Composable
fun rememberProceduralBackdrop(colors: List<Color>): Backdrop =
    rememberCanvasBackdrop {
        // Inside this DrawScope, `size` is the backdrop's own draw area. We
        // paint a diagonal gradient sweep so blur/lens has clear color
        // transitions to bend and frost — flat colors would make the glass
        // effects invisible.
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = colors,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width, size.height)
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
