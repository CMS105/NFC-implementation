package com.example.nfclpgreader.ui.theme

import androidx.compose.ui.graphics.Color

// Midnight Obsidian & Champagne Design System Palette
val ObsidianBg = Color(0xFF0D1322)          // Surface background
val ObsidianSurfaceDim = Color(0xFF080E1D)  // Deep background
val ObsidianContainer = Color(0xFF151B2B)   // Card & ledger surfaces
val ObsidianContainerHigh = Color(0xFF1C2438)
val ElectricCobalt = Color(0xFF4169F5)      // Primary accent
val RoyalCobalt = Color(0xFF244BDB)         // Deep primary gradient
val CobaltGlow = Color(0xFF7595FF)          // High-luminance pulse
val ChampagneGold = Color(0xFFD6B878)       // Accent gold
val ChampagneGoldLight = Color(0xFFFFDF9C)  // Particle sparks
val TextPrimary = Color(0xFFF1F3F9)         // Headlines and primary values
val TextSecondary = Color(0xFF8E9BB0)       // Labels and metadata
val StatusSuccess = Color(0xFF28C76F)       // Verified indicator
val StatusError = Color(0xFFFF4D4F)         // Scan failure / retry
val CardBorder = Color(0xFF222B3D)          // Subtle card outline

// Additional theme aliases
val ObsidianCanvas = ObsidianBg
val ObsidianSurface = ObsidianSurfaceDim
val ObsidianCard = ObsidianContainer
val ObsidianCardElevated = ObsidianContainerHigh
val CobaltPrimary = ElectricCobalt
val CobaltDark = RoyalCobalt
val TextLight = TextPrimary
val TextMuted = TextSecondary
val ObsidianBorder = CardBorder

val ErrorRed = StatusError
val ErrorRedBg = Color(0xFF3B070A)
val WarningAmber = ChampagneGold
val WarningAmberBg = Color(0xFF451A03)

// Backward compatibility aliases
val GasSevaNavy = ElectricCobalt
val GasSevaNavyDark = ObsidianSurfaceDim
val GasSevaGreen = ChampagneGold
val GasSevaGreenDark = ChampagneGold
val GasSevaGreenLight = Color(0x22D6B878)
val BackgroundWhite = ObsidianBg
val SurfaceOffWhite = ObsidianContainer
val TextPrimaryAlias = TextPrimary
val TextSecondaryAlias = TextSecondary
val TextOnNavy = TextPrimary
