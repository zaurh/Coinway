package com.zaurh.coinway.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Default = Typography()

/** Tabular figures keep prices from jiggling as digits change. */
private const val TABULAR = "tnum"

val CoinwayTypography = Typography(
    displaySmall = Default.displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp, fontFeatureSettings = TABULAR),
    headlineLarge = Default.headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp, fontFeatureSettings = TABULAR),
    headlineMedium = Default.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.25).sp),
    headlineSmall = Default.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = TABULAR),
    titleSmall = Default.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = Default.bodyLarge,
    bodyMedium = Default.bodyMedium.copy(fontFeatureSettings = TABULAR),
    bodySmall = Default.bodySmall.copy(fontFeatureSettings = TABULAR),
    labelLarge = Default.labelLarge.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = TABULAR),
    labelMedium = Default.labelMedium.copy(fontWeight = FontWeight.SemiBold, fontFeatureSettings = TABULAR),
    labelSmall = Default.labelSmall.copy(fontWeight = FontWeight.Medium, fontFeatureSettings = TABULAR),
)

internal val TextStyle.tabular: TextStyle get() = copy(fontFeatureSettings = TABULAR)
