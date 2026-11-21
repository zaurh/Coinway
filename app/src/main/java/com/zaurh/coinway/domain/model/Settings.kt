package com.zaurh.coinway.domain.model

enum class ThemeMode { System, Light, Dark }

/** Fiat currencies the app can price coins in. [code] is CoinGecko's `vs_currency`. */
enum class FiatCurrency {
    USD, EUR, GBP, TRY, RUB, JPY, INR, AED;

    val code: String get() = name.lowercase()

    companion object {
        fun fromCode(code: String?): FiatCurrency =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: USD
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val currency: FiatCurrency = FiatCurrency.USD,
)
