package com.zaurh.coinway.presentation.settings

import androidx.lifecycle.viewModelScope
import com.zaurh.coinway.BuildConfig
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.MviViewModel
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val coinRepository: CoinRepository,
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>(
    SettingsState(versionName = BuildConfig.VERSION_NAME)
) {

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { setState { copy(settings = it) } }
        }
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ThemeSelected -> viewModelScope.launch { settingsRepository.setThemeMode(intent.mode) }
            is SettingsIntent.CurrencySelected -> viewModelScope.launch { settingsRepository.setCurrency(intent.currency) }
            SettingsIntent.RateApp -> sendEffect(
                SettingsEffect.OpenStore(
                    marketUri = "market://details?id=${BuildConfig.APPLICATION_ID}",
                    webUrl = STORE_URL,
                )
            )
            SettingsIntent.ShareApp -> sendEffect(
                SettingsEffect.ShareText(UiText.Res(R.string.settings_share_app_text, STORE_URL))
            )
            SettingsIntent.OpenDataSource -> sendEffect(SettingsEffect.OpenUrl(COINGECKO_URL))
            SettingsIntent.ClearCache -> viewModelScope.launch {
                coinRepository.clearCache()
                sendEffect(SettingsEffect.ShowMessage(UiText.Res(R.string.cache_cleared)))
            }
        }
    }

    private companion object {
        const val STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        const val COINGECKO_URL = "https://www.coingecko.com"
    }
}
