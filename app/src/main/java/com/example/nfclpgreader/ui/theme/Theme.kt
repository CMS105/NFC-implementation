package com.example.nfclpgreader.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GasSevaNavy,
    onPrimary = TextOnNavy,
    primaryContainer = GasSevaNavyDark,
    onPrimaryContainer = TextOnNavy,
    secondary = GasSevaGreen,
    onSecondary = TextOnNavy,
    secondaryContainer = GasSevaGreenLight,
    onSecondaryContainer = GasSevaGreenDark,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = BackgroundWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceOffWhite,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = ErrorRed,
    errorContainer = ErrorRedBg
)

@Composable
fun NFCLPGreaderTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = GasSevaNavy.toArgb()
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
