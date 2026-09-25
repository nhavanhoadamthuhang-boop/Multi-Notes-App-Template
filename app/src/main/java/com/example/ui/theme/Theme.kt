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
    primary = M3PrimaryDark,
    onPrimary = M3OnPrimaryDark,
    primaryContainer = M3PrimaryContainerDark,
    onPrimaryContainer = M3OnPrimaryContainerDark,
    secondary = M3SecondaryDark,
    onSecondary = M3OnSecondaryDark,
    secondaryContainer = M3SecondaryContainerDark,
    onSecondaryContainer = M3OnSecondaryContainerDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    tertiary = PinGoldLight,
    tertiaryContainer = Color(0xFF78350F),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = M3PrimaryLight,
    onPrimary = M3OnPrimaryLight,
    primaryContainer = M3PrimaryContainerLight,
    onPrimaryContainer = M3OnPrimaryContainerLight,
    secondary = M3SecondaryLight,
    onSecondary = M3OnSecondaryLight,
    secondaryContainer = M3SecondaryContainerLight,
    onSecondaryContainer = M3OnSecondaryContainerLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    tertiary = PinGold,
    tertiaryContainer = PinGoldContainer,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

private val NightReadingColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFF5D4037),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFFCC80),
    onSecondary = Color(0xFF4E342E),
    secondaryContainer = Color(0xFF6D4C41),
    onSecondaryContainer = Color(0xFFFFECB3),
    background = Color(0xFF191412),
    surface = Color(0xFF241D1A),
    surfaceVariant = Color(0xFF332824),
    onBackground = Color(0xFFEFEBE9),
    onSurface = Color(0xFFEFEBE9),
    onSurfaceVariant = Color(0xFFBCAAA4),
    tertiary = Color(0xFFFFB74D),
    tertiaryContainer = Color(0xFF4E342E),
    outline = Color(0xFF5D4037),
    outlineVariant = Color(0xFF4E342E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    nightReading: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        nightReading -> NightReadingColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
