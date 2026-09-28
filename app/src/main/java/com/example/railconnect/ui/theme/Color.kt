package com.example.railconnect.ui.theme

import androidx.compose.ui.graphics.Color

// Industrial Railway Palette - Dark Theme
val RailwayGraphiteDark = Color(0xFF0D1117)
val RailwaySurfaceDark = Color(0xFF161B22)
val RailwaySurfaceVariantDark = Color(0xFF21262D)
val RailwaySurfaceElevatedDark = Color(0xFF2B323C)
val RailwayBorderDark = Color(0xFF30363D)

// Accents
val IndustrialTeal = Color(0xFF388E83)
val IndustrialTealBright = Color(0xFF4FC3B2)
val IndustrialTealGlow = Color(0x664FC3B2)
val BurnishedBronze = Color(0xFFD4A373)
val BurnishedBronzeLight = Color(0xFFE8C39E)
val WarmIvory = Color(0xFFF7F3E9)
val WarmIvoryMuted = Color(0xFFDCD6C8)

// Rails & Terrain
val SteelRail = Color(0xFFC0CAD3)
val SteelRailShadow = Color(0xFF505A64)
val SleeperWood = Color(0xFF3E3630)
val SleeperComposite = Color(0xFF2A2D32)
val GroundDark = Color(0xFF13171E)
val BlockedDark = Color(0xFF0B0E13)
val BlockedHatch = Color(0xFF242A33)

// Light Theme
val RailwayParchmentLight = Color(0xFFF0F3F5)
val RailwaySurfaceLight = Color(0xFFFFFFFF)
val RailwaySurfaceVariantLight = Color(0xFFE4E9ED)
val RailwayBorderLight = Color(0xFFD0D7DE)
val RailwayPrimaryLight = Color(0xFF1A6B62)
val RailwayOnPrimaryLight = Color(0xFFFFFFFF)

// Board Theme Palettes
data class BoardPalette(
    val boardBackground: Color,
    val gridBorder: Color,
    val groundCell: Color,
    val groundBorder: Color,
    val blockedCell: Color,
    val blockedHatch: Color,
    val sleeperColor: Color,
    val railColor: Color,
    val railHighlightColor: Color,
    val stationPlatform: Color,
    val stationBorder: Color,
    val stationMarker: Color,
    val networkHighlight: Color,
    val networkGlow: Color,
    val selectedBorder: Color
)

val ClassicBoardPalette = BoardPalette(
    boardBackground = Color(0xFF0E1217),
    gridBorder = Color(0xFF232A34),
    groundCell = Color(0xFF161C24),
    groundBorder = Color(0xFF26303D),
    blockedCell = Color(0xFF0A0D11),
    blockedHatch = Color(0xFF222933),
    sleeperColor = Color(0xFF3B332B),
    railColor = Color(0xFFB5BFC9),
    railHighlightColor = Color(0xFF5CD5C5),
    stationPlatform = Color(0xFF272F3B),
    stationBorder = Color(0xFFE8C39E),
    stationMarker = Color(0xFFF5EEDB),
    networkHighlight = Color(0xFF3DB8A8),
    networkGlow = Color(0x773DB8A8),
    selectedBorder = Color(0xFFE5B887)
)

val MidnightBoardPalette = BoardPalette(
    boardBackground = Color(0xFF080D14),
    gridBorder = Color(0xFF152233),
    groundCell = Color(0xFF0E1825),
    groundBorder = Color(0xFF1C2D44),
    blockedCell = Color(0xFF05090E),
    blockedHatch = Color(0xFF172436),
    sleeperColor = Color(0xFF222B38),
    railColor = Color(0xFF90A4AE),
    railHighlightColor = Color(0xFF00E5FF),
    stationPlatform = Color(0xFF1A2A40),
    stationBorder = Color(0xFF80DEEA),
    stationMarker = Color(0xFFE0F7FA),
    networkHighlight = Color(0xFF00BCD4),
    networkGlow = Color(0x8800BCD4),
    selectedBorder = Color(0xFF4DD0E1)
)