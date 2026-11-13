package com.example.nfclpgreader.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ObsidianChampagneColorScheme = darkColorScheme(
    primary = CobaltPrimary,
    onPrimary = TextLight,
    primaryContainer = CobaltDark,
    onPrimaryContainer = TextLight,
    secondary = ChampagneGold,
    onSecondary = ObsidianCanvas,
    secondaryContainer = ObsidianSurface,
    onSecondaryContainer = ChampagneGold,
    background = ObsidianCanvas,
    onBackground = TextLight,
    surface = ObsidianSurface,
    onSurface = TextLight,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextMuted,
    outline = ObsidianBorder,
    error = ErrorRed,
    errorContainer = ErrorRedBg
)

@Composable
fun NFCLPGreaderTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ObsidianChampagneColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = ObsidianCanvas.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
