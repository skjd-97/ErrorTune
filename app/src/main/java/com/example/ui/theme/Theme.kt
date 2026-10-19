package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HackerColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF001F29),
    primaryContainer = Color(0xFF004D5C),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = TerminalGreen,
    onSecondary = Color(0xFF00220B),
    secondaryContainer = Color(0xFF005322),
    onSecondaryContainer = Color(0xFF6BFF9A),
    tertiary = CyberPurple,
    onTertiary = Color(0xFF28004F),
    tertiaryContainer = Color(0xFF55198C),
    onTertiaryContainer = Color(0xFFE9D5FF),
    background = MatrixDark,
    onBackground = HackerText,
    surface = TechSurface,
    onSurface = HackerText,
    surfaceVariant = TechSurfaceElevated,
    onSurfaceVariant = HackerTextSecondary,
    outline = TechBorder,
    error = GlitchRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep hacker colors consistent
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HackerColorScheme,
        typography = Typography,
        content = content
    )
}
