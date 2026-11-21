package com.zaurh.coinway.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zaurh.coinway.domain.model.AppSettings
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.ThemeMode
import com.zaurh.coinway.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            AppSettings(
                themeMode = ThemeMode.entries.firstOrNull { it.name == prefs[THEME] } ?: ThemeMode.System,
                currency = FiatCurrency.fromCode(prefs[CURRENCY]),
            )
        }
        .distinctUntilChanged()

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME] = mode.name }
    }

    override suspend fun setCurrency(currency: FiatCurrency) {
        dataStore.edit { it[CURRENCY] = currency.code }
    }

    private companion object {
        val THEME = stringPreferencesKey("theme_mode")
        val CURRENCY = stringPreferencesKey("currency")
    }
}
