package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = NeonTeal,
    secondary = WarningOrange,
    tertiary = SoftPurple,
    background = TerminalBackground,
    surface = TerminalSurface,
    onBackground = OnBackgroundBright,
    onSurface = OnSurfaceMuted,
    surfaceVariant = TerminalSurfaceVariant,
    outline = TerminalOutline,
    error = TerminalError
)

// Aesthetic high-contrast industrial light theme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A68),
    secondary = Color(0xFFD34F1A),
    tertiary = Color(0xFF6D4ABB),
    background = Color(0xFFF4F6F9),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF13151A),
    onSurface = Color(0xFF4B5563),
    surfaceVariant = Color(0xFFE5E7EB),
    outline = Color(0xFFD1D5DB),
    error = Color(0xFFDC2626)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force our custom theme for consistent Brutalist branded look!
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Push OLED Dark terminal by default as requested!
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
