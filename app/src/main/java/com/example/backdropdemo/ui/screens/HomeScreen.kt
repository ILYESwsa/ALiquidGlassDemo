package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.TintedGlassIconButton
import com.example.backdropdemo.ui.theme.AccentBlue
import com.example.backdropdemo.ui.theme.AccentPink
import com.kyant.backdrop.Backdrop

/**
 * ─────────────────────────────────────────────────────────────────────────
 * HOME SCREEN — landing section. Shows:
 *   • A large `GlassSurface` "hero" card explaining the library.
 *   • A row of `TintedGlassIconButton`s (BlendMode.Hue tint showcase).
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun HomeScreen(backdrop: Backdrop) {
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
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
                            "Switch tabs below to see bottom sheet, slider and " +
                            "button variants.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Text(
                "Tinted glass buttons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val icons = listOf(
                    Icons.Filled.Favorite to AccentPink,
                    Icons.Filled.Notifications to AccentBlue,
                    Icons.Filled.Share to Color(0xFF34D399),
                    Icons.Filled.Settings to Color(0xFFFFB020)
                )
                icons.forEach { (icon, tint) ->
                    TintedGlassIconButton(
                        backdrop = backdrop,
                        tint = tint,
                        size = 56.dp
                    ) {
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
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
                        "rememberCombinedBackdrop — merges scene + floating layers for the bottom sheet",
                        "rememberCanvasBackdrop — a hand-drawn procedural backdrop for the glass slider",
                        "emptyBackdrop — safe placeholder default for optional glass surfaces",
                        "drawBackdrop + vibrancy/blur/lens — the actual glass rendering pipeline"
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
