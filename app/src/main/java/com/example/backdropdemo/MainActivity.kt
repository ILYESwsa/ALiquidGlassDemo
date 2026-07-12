package com.example.backdropdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.backdropdemo.ui.screens.BackdropDemoApp
import com.example.backdropdemo.ui.theme.BackdropDemoTheme

/**
 * Entry point. Edge-to-edge is enabled so the gradient scene and glass
 * bottom bar can extend behind the system bars — glass effects read much
 * better when they aren't boxed in by opaque system chrome.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackdropDemoTheme {
                BackdropDemoApp()
            }
        }
    }
}
