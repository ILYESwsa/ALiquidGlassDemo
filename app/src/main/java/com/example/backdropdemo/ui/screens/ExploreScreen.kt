package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.backdropdemo.ui.backdrop.GlassSurface
import com.example.backdropdemo.ui.backdrop.rememberFallbackBackdrop
import com.kyant.backdrop.Backdrop

/**
 * ─────────────────────────────────────────────────────────────────────────
 * EXPLORE SCREEN — a grid of glass cards over the shared scene backdrop,
 * plus a toggle that swaps one card to `emptyBackdrop()` to visually show
 * the difference: an empty backdrop has nothing to refract, so the glass
 * effects fall back to just the flat surface tint (still valid, just not
 * "liquid").
 * ─────────────────────────────────────────────────────────────────────────
 */
@Composable
fun ExploreScreen(backdrop: Backdrop) {
    // Toggled by tapping the first card — swaps its backdrop source so you
    // can directly compare a real capture vs. `emptyBackdrop()`.
    var useEmptyBackdropForFirstCard by remember { mutableStateOf(false) }
    val fallback = rememberFallbackBackdrop()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp, 32.dp, 20.dp, 140.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(Modifier.safeContentPadding()) {
                Text(
                    "Explore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Tap the first card to compare a real backdrop against emptyBackdrop().",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        items(8) { index ->
            val isFirst = index == 0
            GlassSurface(
                backdrop = if (isFirst && useEmptyBackdropForFirstCard) fallback else backdrop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(enabled = isFirst) {
                        useEmptyBackdropForFirstCard = !useEmptyBackdropForFirstCard
                    },
                tint = Color.White.copy(alpha = 0.15f)
            ) {
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    Text(
                        text = when {
                            !isFirst -> "Card ${index + 1}"
                            useEmptyBackdropForFirstCard -> "emptyBackdrop()"
                            else -> "Tap me"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = when {
                            !isFirst -> "Liquid glass card"
                            useEmptyBackdropForFirstCard -> "No pixels to refract — flat tint only."
                            else -> "Real backdrop — see the gradient bend."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
