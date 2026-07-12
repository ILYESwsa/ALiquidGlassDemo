package com.example.backdropdemo.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

/** The four tabs shown in the glass bottom bar, each mapped to a demo section. */
enum class Destination(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.Home),
    Explore("explore", "Explore", Icons.Filled.Search),
    Sheet("sheet", "Sheet", Icons.Filled.Widgets),
    Controls("controls", "Controls", Icons.Filled.Tune)
}
