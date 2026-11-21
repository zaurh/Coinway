package com.zaurh.coinway.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zaurh.coinway.domain.model.ThemeMode

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF2B1A00),
    primaryContainer = Color(0xFF3D2A0E),
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = Color(0xFFC9C1F2),
    onSecondary = Color(0xFF2A2546),
    secondaryContainer = Night4,
    onSecondaryContainer = NightText,
    background = Night0,
    onBackground = NightText,
    surface = Night0,
    onSurface = NightText,
    surfaceVariant = Night3,
    onSurfaceVariant = NightTextMuted,
    surfaceContainerLowest = Night0,
    surfaceContainerLow = Night1,
    surfaceContainer = Night1,
    surfaceContainerHigh = Night2,
    surfaceContainerHighest = Night3,
    outline = Night4,
    outlineVariant = Night3,
    inverseSurface = NightText,
    inverseOnSurface = Night1,
    inversePrimary = GoldDeep,
    error = LossDark,
)

private val LightColors = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3BF),
    onPrimaryContainer = Color(0xFF2B1A00),
    secondary = Color(0xFF5A5379),
    onSecondary = Color.White,
    secondaryContainer = Day3,
    onSecondaryContainer = DayText,
    background = Day0,
    onBackground = DayText,
    surface = Day0,
    onSurface = DayText,
    surfaceVariant = Day3,
    onSurfaceVariant = DayTextMuted,
    surfaceContainerLowest = Day1,
    surfaceContainerLow = Day1,
    surfaceContainer = Day1,
    surfaceContainerHigh = Day2,
    surfaceContainerHighest = Day3,
    outline = Day4,
    outlineVariant = Day3,
    inverseSurface = Night1,
    inverseOnSurface = NightText,
    inversePrimary = Gold,
    error = LossLight,
)

/** Colors Material doesn't have a slot for. */
@Immutable
data class MarketColors(val gain: Color, val loss: Color, val neutral: Color) {
    fun forChange(change: Double?): Color = when {
        change == null || change == 0.0 -> neutral
        change > 0 -> gain
        else -> loss
    }
}

private val LocalMarketColors = staticCompositionLocalOf {
    MarketColors(GainDark, LossDark, NightTextMuted)
}

private val CoinwayShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

object CoinwayTheme {
    val marketColors: MarketColors
        @Composable get() = LocalMarketColors.current
}

@Composable
fun CoinwayTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val colors: ColorScheme = if (dark) DarkColors else LightColors
    val marketColors = if (dark) {
        MarketColors(GainDark, LossDark, NightTextMuted)
    } else {
        MarketColors(GainLight, LossLight, DayTextMuted)
    }

    CompositionLocalProvider(LocalMarketColors provides marketColors) {
        MaterialTheme(
            colorScheme = colors,
            typography = CoinwayTypography,
            shapes = CoinwayShapes,
            content = content,
        )
    }
}

fun ThemeMode.isDark(systemDark: Boolean): Boolean = when (this) {
    ThemeMode.System -> systemDark
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
