package com.example.nfclpgreader.ui.theme

import androidx.compose.ui.graphics.Color

// GasSeva Luxury Hardware Palette (Matching gasseva_ui_ux_redesign_prototype.html)
val ObsidianBg = Color(0xFF0B0B0D)          // Matte Carbon Black Screen BG
val ObsidianSurfaceDim = Color(0xFF070708)  // Deep Studio BG
val ObsidianContainer = Color(0xFF141418)   // Surface 1 Glass Card
val ObsidianContainerHigh = Color(0xFF1C2438) // Surface 2 Elevated

val CardGradTop = Color(0xFF18181D)         // Card Gradient Top
val CardGradBot = Color(0xFF0F0F13)         // Card Gradient Bottom

val GoldLight = Color(0xFFF3D9A4)           // Brushed Champagne Gold Light
val GoldMid = Color(0xFFDFBD7E)             // Brushed Champagne Gold Mid
val GoldDeep = Color(0xFFC9A25B)            // Brushed Champagne Gold Deep
val GoldInk = Color(0xFF0B0B0D)             // Jet Black Ink for Gold Buttons

val ChampagneGold = GoldMid
val ChampagneGoldLight = GoldLight
val ChampagneGoldDeep = GoldDeep
val ShiningGold = GoldLight

val TextPrimary = Color(0xFFF6F4EF)         // Champagne Ice White
val TextSecondary = Color(0xFF9C968B)       // Warm Muted Taupe
val TextMuted = Color(0xFF6B665E)           // Deep Muted Silver

val EmeraldText = Color(0xFF34D399)         // Emerald Verified Text
val EmeraldBg = Color(0x1C10B981)           // Emerald Tint Background
val EmeraldBorder = Color(0x4710B981)       // Emerald Tint Border

val GoldBorder = Color(0x38DFBD7E)          // Gold Hairline Border
val GoldBorderStrong = Color(0x7ADFBD7E)    // Gold Strong Border
val GoldTintBg = Color(0x17DFBD7E)          // Gold Tint Background
val CardBorder = GoldBorder

val StatusSuccess = EmeraldText
val StatusError = Color(0xFFF43F5E)         // Rose Crimson Error
val ErrorRed = StatusError
val ErrorRedBg = Color(0xFF2A0D12)

// Backward Compatibility Aliases
val ElectricCobalt = GoldMid
val RoyalCobalt = GoldDeep
val CobaltGlow = GoldLight

val ObsidianCanvas = ObsidianBg
val ObsidianSurface = ObsidianSurfaceDim
val ObsidianCard = ObsidianContainer
val ObsidianCardElevated = ObsidianContainerHigh
val CobaltPrimary = GoldMid
val CobaltDark = GoldDeep
val TextLight = TextPrimary
val TextMutedAlias = TextMuted
val ObsidianBorder = CardBorder

val WarningAmber = GoldMid
val WarningAmberBg = GoldTintBg

val GasSevaNavy = GoldMid
val GasSevaNavyDark = ObsidianSurfaceDim
val GasSevaGreen = EmeraldText
val GasSevaGreenDark = Color(0xFF059669)
val GasSevaGreenLight = EmeraldBg
val BackgroundWhite = ObsidianBg
val SurfaceOffWhite = ObsidianContainer
val TextPrimaryAlias = TextPrimary
val TextSecondaryAlias = TextSecondary
val TextOnNavy = GoldInk
