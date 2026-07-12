# Backdrop Demo — Liquid Glass with CMP Backdrop

A small, focused Jetpack Compose app that showcases the **[Backdrop](https://github.com/Kyant0/AndroidLiquidGlass)**
library (`io.github.kyant0:backdrop-android`) — a Compose Multiplatform
liquid-glass effects toolkit. This is a demo of the *library's* capabilities,
not a real app: every screen exists to show off one glass technique.

## Install

```kotlin
// module build.gradle.kts
dependencies {
    implementation("io.github.kyant0:backdrop-android:2.0.0-alpha03")
}
```

Maven Central is enabled by default in most `settings.gradle.kts` setups
(`mavenCentral()` in `dependencyResolutionManagement`). No extra repository
is required.

> The library is still in alpha (`2.0.0-alpha03`) — expect API changes
> between versions. Advanced shader effects (vibrancy, lens) use AGSL and
> require API 33+ to look their best; on older API levels the library
> gracefully degrades (see `minSdk = 24` in this project).

## What each screen demonstrates

| Screen | Backdrop concept | Library APIs |
|---|---|---|
| **App shell / Home** | One shared backdrop records the whole scene; the bottom bar reads from it | `rememberLayerBackdrop`, `Modifier.layerBackdrop`, `Modifier.drawBackdrop` |
| **Interactive Glass Bottom Bar** | Pressed pills scale visually without shifting what they refract | `drawBackdrop(layerBlock = ...)` |
| **Explore** | Comparing a real backdrop against no backdrop at all | `emptyBackdrop()` |
| **Glass Bottom Sheet** | Merging the app scene with the sheet's own recorded layer | `rememberCombinedBackdrop` |
| **Glass Slider** | A hand-painted procedural backdrop independent of any UI layer | `rememberCanvasBackdrop` |
| **Tinted glass icon buttons** | Coloring glass without losing its "see-through" quality | `BlendMode.Hue` inside `onDrawSurface` |

## The core mental model

1. **Record** — `rememberLayerBackdrop()` (or `rememberCanvasBackdrop` /
   `rememberCombinedBackdrop`) creates a `Backdrop`. Attach it to whatever
   should be "seen through" later with `Modifier.layerBackdrop(backdrop)`.
2. **Refract** — any composable that should look like glass uses
   `Modifier.drawBackdrop(backdrop, shape, effects, onDrawSurface)`.
   Inside `effects { }` you chain:
   - `vibrancy()` — boosts saturation/contrast of the sampled pixels, like
     light passing through frosted glass.
   - `blur(radiusPx)` — frosts the backdrop; higher radius = thicker glass.
   - `lens(refractionHeight, refractionAmount, chromaticAberration)` — bends
     pixels near the shape's edge to fake a curved glass surface, optionally
     with a subtle RGB split for realism.
3. **Tint for readability** — `onDrawSurface { drawRect(...) }` runs *after*
   the effects and is what keeps foreground text/icons legible. Without a
   translucent tint layer, refracted-and-blurred pixels alone can look
   muddy and low-contrast.
4. **Color without losing the glass** — `BlendMode.Hue` (used in
   `TintedGlassIconButton`) paints only the hue of a tint color onto the
   refracted pixels, preserving their original brightness/shadows — the
   difference between "colored glass" and "a flat colored circle."

All reusable glass building blocks live in
`app/src/main/java/com/example/backdropdemo/ui/backdrop/`, heavily commented
so each effect's purpose is clear at the call site. Screens in
`ui/screens/` are intentionally simple — the interesting code is the glass,
not the app logic around it.

## Building

### Locally
```bash
./gradlew assembleDebug
```
The APK is written to `app/build/outputs/apk/debug/`.

### CI
Pushing to `main` (or running the workflow manually from the **Actions** tab)
triggers `.github/workflows/build.yml`, which assembles a debug APK and
uploads it as a build artifact named `backdrop-demo-debug-apk`.

## Requirements

- Android Studio Koala+ / Gradle 8.7 / JDK 17
- `compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`
- Kotlin 1.9.24 with the Compose compiler plugin
