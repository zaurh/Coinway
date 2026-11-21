package com.zaurh.coinway.presentation.markets

import com.zaurh.coinway.core.util.AppError
import com.zaurh.coinway.core.util.AppException
import com.zaurh.coinway.domain.model.AppSettings
import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.GlobalMarket
import com.zaurh.coinway.domain.model.PricePoint
import com.zaurh.coinway.domain.model.ThemeMode
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

fun coin(id: String, rank: Int, price: Double, change: Double, name: String = id.replaceFirstChar { it.uppercase() }) = Coin(
    id = id,
    symbol = id.take(3),
    name = name,
    imageUrl = "",
    price = price,
    marketCap = price * 1000,
    rank = rank,
    volume = 0.0,
    change24h = change,
    sparkline = emptyList(),
    currency = "usd",
    updatedAt = 0L,
)

class FakeCoinRepository(
    private val remote: List<Coin> = emptyList(),
) : CoinRepository {
    val cache = MutableStateFlow<List<Coin>>(emptyList())
    val favorites = MutableStateFlow<Set<String>>(emptySet())
    var refreshError: AppError? = null
    val refreshedCurrencies = mutableListOf<FiatCurrency>()

    private val withFavorites = combine(cache, favorites) { coins, favs ->
        coins.map { it.copy(isFavorite = it.id in favs) }
    }

    override fun observeCoins(): Flow<List<Coin>> = withFavorites
    override fun observeWatchlist(): Flow<List<Coin>> = withFavorites.map { list -> list.filter { it.isFavorite } }
    override fun observeCoin(id: String): Flow<Coin?> = withFavorites.map { list -> list.find { it.id == id } }
    override fun observeIsFavorite(id: String): Flow<Boolean> = favorites.map { id in it }

    override suspend fun refreshMarkets(currency: FiatCurrency): Result<Unit> {
        refreshedCurrencies += currency
        refreshError?.let { return Result.failure(AppException(it)) }
        cache.value = remote.map { it.copy(currency = currency.code) }
        return Result.success(Unit)
    }

    override suspend fun refreshWatchlist(currency: FiatCurrency) = Result.success(Unit)

    override suspend fun getGlobalMarket(currency: FiatCurrency) =
        Result.success(GlobalMarket(1e12, 1e10, 1.5, 55.0, currency.code))

    override suspend fun getCoinDetail(id: String, currency: FiatCurrency): Result<CoinDetail> =
        Result.failure(AppException(AppError.Unknown))

    override suspend fun getPriceHistory(id: String, currency: FiatCurrency, range: ChartRange): Result<List<PricePoint>> =
        Result.success(emptyList())

    override suspend fun toggleFavorite(id: String): Boolean {
        val now = id !in favorites.value
        setFavorite(id, now)
        return now
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        favorites.update { if (favorite) it + id else it - id }
    }

    override suspend fun clearCache() {
        cache.value = emptyList()
    }
}

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val settings: Flow<AppSettings> = state
    override suspend fun setThemeMode(mode: ThemeMode) = state.update { it.copy(themeMode = mode) }
    override suspend fun setCurrency(currency: FiatCurrency) = state.update { it.copy(currency = currency) }
}
