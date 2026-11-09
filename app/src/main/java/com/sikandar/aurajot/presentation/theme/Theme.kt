package com.sikandar.aurajot.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AuraAmber,
    onPrimary = ObsidianBlack,
    primaryContainer = AuraAmberGlow,
    onPrimaryContainer = AuraAmberBright,
    secondary = CelestialAzure,
    onSecondary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = EditorialWhite,
    surface = ObsidianSurface,
    onSurface = EditorialWhite,
    surfaceVariant = BentoGlassBg,
    onSurfaceVariant = SlateLight,
    error = CrimsonAlert,
    onError = EditorialWhite
)

@Composable
fun AuraJotTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = ObsidianBlack.toArgb()
            window.navigationBarColor = ObsidianBlack.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
