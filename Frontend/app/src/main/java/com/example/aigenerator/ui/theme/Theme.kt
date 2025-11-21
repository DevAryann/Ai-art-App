package com.example.aigenerator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// We define the Dark Scheme using our Cyber colors
private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = ElectricBlue,
    tertiary = NeonPink,
    background = CyberBlack,
    surface = CyberSurface,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun AiGeneratorTheme(
    // FORCE Dark theme by default for that "AI look"
    darkTheme: Boolean = true,
    // Disable dynamic color so our Neon colors don't get overwritten by wallpaper
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Since we forced darkTheme = true above, this will always run
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}