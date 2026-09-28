package com.example.railconnect.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.railconnect.data.AppThemeMode
import com.example.railconnect.data.BoardThemeMode

val LocalBoardPalette = staticCompositionLocalOf { ClassicBoardPalette }

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = IndustrialTealBright,
    onPrimary = Color(0xFF003732),
    primaryContainer = IndustrialTeal,
    onPrimaryContainer = Color(0xFFA1F2E5),
    secondary = BurnishedBronze,
    onSecondary = Color(0xFF3E2200),
    secondaryContainer = Color(0xFF5A3610),
    onSecondaryContainer = BurnishedBronzeLight,
    background = RailwayGraphiteDark,
    onBackground = WarmIvory,
    surface = RailwaySurfaceDark,
    onSurface = WarmIvory,
    surfaceVariant = RailwaySurfaceVariantDark,
    onSurfaceVariant = WarmIvoryMuted,
    outline = RailwayBorderDark
)

private val IndustrialLightColorScheme = lightColorScheme(
    primary = RailwayPrimaryLight,
    onPrimary = RailwayOnPrimaryLight,
    primaryContainer = Color(0xFF9FF2E6),
    onPrimaryContainer = Color(0xFF00201D),
    secondary = Color(0xFF7E4A15),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDB8),
    onSecondaryContainer = Color(0xFF2B1400),
    background = RailwayParchmentLight,
    onBackground = Color(0xFF191C1E),
    surface = RailwaySurfaceLight,
    onSurface = Color(0xFF191C1E),
    surfaceVariant = RailwaySurfaceVariantLight,
    onSurfaceVariant = Color(0xFF40484B),
    outline = RailwayBorderLight
)

@Composable
fun RailConnectTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    boardTheme: BoardThemeMode = BoardThemeMode.CLASSIC,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) IndustrialDarkColorScheme else IndustrialLightColorScheme
    val boardPalette = when (boardTheme) {
        BoardThemeMode.CLASSIC -> ClassicBoardPalette
        BoardThemeMode.MIDNIGHT -> MidnightBoardPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(LocalBoardPalette provides boardPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}