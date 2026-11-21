package com.zaurh.coinway.presentation.settings

import com.zaurh.coinway.core.mvi.UiEffect
import com.zaurh.coinway.core.mvi.UiIntent
import com.zaurh.coinway.core.mvi.UiState
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.domain.model.AppSettings
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.ThemeMode

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val versionName: String = "",
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class ThemeSelected(val mode: ThemeMode) : SettingsIntent
    data class CurrencySelected(val currency: FiatCurrency) : SettingsIntent
    data object RateApp : SettingsIntent
    data object ShareApp : SettingsIntent
    data object OpenDataSource : SettingsIntent
    data object ClearCache : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    /** Opens the Play Store listing, falling back to the web page. */
    data class OpenStore(val marketUri: String, val webUrl: String) : SettingsEffect
    data class OpenUrl(val url: String) : SettingsEffect
    data class ShareText(val text: UiText) : SettingsEffect
    data class ShowMessage(val text: UiText) : SettingsEffect
}
