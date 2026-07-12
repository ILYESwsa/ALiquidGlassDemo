package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.backdropdemo.ui.backdrop.DecorativeBackground
import com.example.backdropdemo.ui.backdrop.InteractiveGlassPill
import com.example.backdropdemo.ui.backdrop.SceneBackground
import com.example.backdropdemo.ui.backdrop.rememberSceneBackdrop

/**
 * ─────────────────────────────────────────────────────────────────────────
 * SECTION 1 & 2: "Glass Bottom Bar over a main nav host" +
 *                "Interactive Glass Bottom Bar"
 * ─────────────────────────────────────────────────────────────────────────
 *
 * This is the app's shell. The pattern (straight from the library's own
 * tutorial) is:
 *
 *   1. Create ONE backdrop with `rememberLayerBackdrop()` at the shell level.
 *   2. Attach it to the layer behind the NavHost via
 *      `Modifier.layerBackdrop(backdrop)` (done inside `SceneBackground`).
 *   3. Draw the bottom bar ON TOP, using `drawBackdrop(backdrop, ...)` so it
 *      refracts/blurs whatever screen is currently showing underneath it.
 *
 * Because the backdrop is recorded continuously, navigating between tabs
 * changes what the glass bar refracts in real time — the glass always
 * reflects the *current* screen content, not a static snapshot.
 */
@Composable
fun BackdropDemoApp() {
    val navController = rememberNavController()
    // The ONE scene backdrop shared by the whole shell — every screen draws
    // into it, and the glass bar below reads from it.
    val backdrop = rememberSceneBackdrop()

    Box(Modifier.fillMaxSize()) {
        // Background + NavHost are both captured into `backdrop`.
        SceneBackground(backdrop = backdrop) {
            DecorativeBackground()
            NavHost(
                navController = navController,
                startDestination = Destination.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Destination.Home.route) { HomeScreen(backdrop) }
                composable(Destination.Explore.route) { ExploreScreen(backdrop) }
                composable(Destination.Sheet.route) { GlassBottomSheetScreen(backdrop) }
                composable(Destination.Controls.route) { GlassControlsScreen(backdrop) }
            }
        }

        // The glass bottom bar itself: drawn OUTSIDE the layerBackdrop scope
        // (so it isn't recursively captured into its own backdrop) but still
        // reads from `backdrop` to render its liquid glass surface.
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        Row(
            modifier = Modifier
                .safeContentPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(64.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destination.entries.forEach { destination ->
                InteractiveGlassPill(
                    backdrop = backdrop,
                    selected = currentRoute == destination.route,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = Color.White
                        )
                        Text(
                            text = destination.label,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
