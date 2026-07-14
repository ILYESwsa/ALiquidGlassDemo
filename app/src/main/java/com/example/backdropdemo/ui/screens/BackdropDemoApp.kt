package com.example.backdropdemo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.backdropdemo.ui.backdrop.GlassBottomTab
import com.example.backdropdemo.ui.backdrop.GlassBottomTabs
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
 *   3. Draw the bottom bar ON TOP with `GlassBottomTabs`, which internally
 *      uses `drawBackdrop(backdrop, ...)` so it refracts/blurs whatever
 *      screen is currently showing underneath it, plus a draggable floating
 *      selection pill that refracts a combined view of the scene AND the
 *      tab bar's own icons/labels.
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

    var selectedIndex by remember { mutableIntStateOf(0) }
    val destinations = Destination.entries

    Box(Modifier.fillMaxSize()) {
        // Background is captured into `backdrop`. It renders ONLY the
        // gradient + decorative shapes — no glass surfaces live inside it.
        SceneBackground(backdrop = backdrop)

        // The NavHost is a SIBLING of SceneBackground, not a child of it.
        // Its screens use `backdrop` to draw glass (via GlassSurface etc.),
        // which is only safe because they sit outside the layer that
        // `backdrop` itself records — see SceneBackground's doc comment for
        // why nesting them would cause a native stack-overflow crash.
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

        // The glass bottom bar: a draggable floating pill that can be
        // flicked directly between tabs, or tapped to animate there. Drawn
        // OUTSIDE the layerBackdrop scope (so it isn't recursively captured
        // into its own backdrop) but still reads from `backdrop`.
        GlassBottomTabs(
            selectedTabIndex = { selectedIndex },
            onTabSelected = { index ->
                selectedIndex = index
                val destination = destinations[index]
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            backdrop = backdrop,
            tabsCount = destinations.size,
            modifier = Modifier
                .safeContentPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            destinations.forEachIndexed { index, destination ->
                GlassBottomTab(
                    onClick = { selectedIndex = index }
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
