package com.example.smartgardenapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = TextLight,
    primaryContainer = CardGreen,
    onPrimaryContainer = GreenDark,
    secondary = BluePrimary,
    onSecondary = TextLight,
    secondaryContainer = CardBlue,
    onSecondaryContainer = BlueDark,
    tertiary = TemperatureColor,
    onTertiary = TextLight,
    tertiaryContainer = CardOrange,
    onTertiaryContainer = TextPrimary,
    error = ErrorColor,
    onError = TextLight,
    errorContainer = CardRed,
    onErrorContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = CardGreen,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = TextPrimary,
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenAccent,
    secondary = BlueLight,
    onSecondary = TextPrimary,
    secondaryContainer = BlueDark,
    onSecondaryContainer = BlueLight,
    tertiary = TemperatureColor,
    onTertiary = TextPrimary,
    tertiaryContainer = CardOrange,
    onTertiaryContainer = TextPrimary,
    error = ErrorColor,
    onError = TextLight,
    errorContainer = CardRed,
    onErrorContainer = TextPrimary,
    background = BackgroundDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextMuted,
    outline = TextMuted
)

@Composable
fun SmartGardenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

