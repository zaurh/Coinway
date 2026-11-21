package com.zaurh.coinway.domain.repository

import com.zaurh.coinway.domain.model.AppSettings
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setCurrency(currency: FiatCurrency)
}
