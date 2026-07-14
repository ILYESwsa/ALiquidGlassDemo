package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.backdropdemo.ui.backdrop.GlassButton
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.GlassToggle
import com.example.backdropdemo.ui.theme.AccentBlue
import com.example.backdropdemo.ui.theme.AccentPink
import com.kyant.backdrop.Backdrop

/**
 * ─────────────────────────────────────────────────────────────────────────
 * HOME SCREEN — landing section. Shows:
 *   • A large `GlassSurface` "hero" card explaining the library.
 *   • `GlassButton`s: a plain "Surface" pill and two `BlendMode.Hue`-tinted
 *     pills. Press and HOLD one — the glass leans toward your finger and
 *     stretches slightly, then springs back on release.
 *   • A `GlassToggle` — press-and-hold squashes the thumb; drag it directly
 *     or tap either side to flip it.
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun HomeScreen(backdrop: Backdrop) {
    var toggled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 20.dp,
            end = 20.dp,
            bottom = 140.dp // leave room for the floating glass bottom bar
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(Modifier.safeContentPadding()) {
                Text(
                    "Liquid Glass",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "CMP Backdrop demo",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        item {
            GlassSurface(
                backdrop = backdrop,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                tint = Color.White.copy(alpha = 0.14f)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "What you're looking at",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        "Every translucent panel on this screen is a real-time " +
                            "backdrop capture — blurred, refracted (lens), and " +
                            "saturated (vibrancy) from the gradient and shapes " +
                            "behind it, then tinted so text stays readable. " +
                            "Press and hold a button below to feel the glass react.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Text(
                "Liquid glass buttons — press and hold",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassButton(
                    onClick = {},
                    backdrop = backdrop,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Surface Liquid Button", color = Color.White, fontWeight = FontWeight.Medium)
                }
                GlassButton(
                    onClick = {},
                    backdrop = backdrop,
                    tint = AccentBlue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tinted Liquid Button", color = Color.White, fontWeight = FontWeight.Medium)
                }
                GlassButton(
                    onClick = {},
                    backdrop = backdrop,
                    tint = AccentPink,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tinted Liquid Button", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }

        item {
            GlassSurface(
                backdrop = backdrop,
                modifier = Modifier.fillMaxWidth(),
                tint = Color.White.copy(alpha = 0.12f)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Liquid glass toggle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            "Drag it, tap it, or hold it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    GlassToggle(
                        selected = { toggled },
                        onSelect = { toggled = it },
                        backdrop = backdrop
                    )
                }
            }
        }

        item {
            GlassSurface(
                backdrop = backdrop,
                modifier = Modifier.fillMaxWidth(),
                tint = Color.White.copy(alpha = 0.12f)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Core APIs used in this app",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    listOf(
                        "rememberBackdrop / rememberLayerBackdrop — records the scene behind the NavHost",
                        "rememberCombinedBackdrop — merges the scene with a sibling's own backdrop (toggle, tabs, slider)",
                        "rememberCanvasBackdrop — a hand-drawn procedural backdrop",
                        "emptyBackdrop — safe placeholder default for optional glass surfaces",
                        "drawBackdrop + vibrancy/blur/lens — the actual glass rendering pipeline",
                        "highlight / shadow / innerShadow — extra depth cues on interactive controls"
                    ).forEach { line ->
                        Text(
                            "• $line",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
