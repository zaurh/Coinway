package com.zaurh.coinway.data.repository

import com.zaurh.coinway.core.util.appRunCatching
import com.zaurh.coinway.data.local.CoinDao
import com.zaurh.coinway.data.local.FavoriteEntity
import com.zaurh.coinway.data.remote.CoinGeckoApi
import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.GlobalMarket
import com.zaurh.coinway.domain.model.PricePoint
import com.zaurh.coinway.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val dao: CoinDao,
) : CoinRepository {

    override fun observeCoins(): Flow<List<Coin>> =
        dao.observeCoins().map { rows -> rows.map { it.toDomain() } }

    override fun observeWatchlist(): Flow<List<Coin>> =
        dao.observeWatchlist().map { rows -> rows.map { it.toDomain() } }

    override fun observeCoin(id: String): Flow<Coin?> =
        dao.observeCoin(id).map { it?.toDomain() }.distinctUntilChanged()

    override fun observeIsFavorite(id: String): Flow<Boolean> =
        dao.observeIsFavorite(id).distinctUntilChanged()

    override suspend fun refreshMarkets(currency: FiatCurrency): Result<Unit> = appRunCatching {
        val now = System.currentTimeMillis()
        val coins = api.getMarkets(currency = currency.code)
        dao.replaceMarket(coins.map { it.toEntity(currency.code, now) })
    }

    override suspend fun refreshWatchlist(currency: FiatCurrency): Result<Unit> = appRunCatching {
        val ids = dao.favoriteIds()
        if (ids.isEmpty()) return@appRunCatching
        val now = System.currentTimeMillis()
        val coins = api.getMarkets(currency = currency.code, ids = ids.joinToString(","))
        dao.upsertCoins(coins.map { it.toEntity(currency.code, now) })
    }

    override suspend fun getGlobalMarket(currency: FiatCurrency): Result<GlobalMarket> = appRunCatching {
        api.getGlobal().data.toDomain(currency.code)
    }

    override suspend fun getCoinDetail(id: String, currency: FiatCurrency): Result<CoinDetail> = appRunCatching {
        api.getCoin(id).toDomain(currency.code)
    }

    override suspend fun getPriceHistory(
        id: String,
        currency: FiatCurrency,
        range: ChartRange,
    ): Result<List<PricePoint>> = appRunCatching {
        api.getMarketChart(id = id, currency = currency.code, days = range.days).toDomain()
    }

    override suspend fun toggleFavorite(id: String): Boolean {
        val favorite = !dao.isFavorite(id)
        setFavorite(id, favorite)
        return favorite
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        if (favorite) dao.addFavorite(FavoriteEntity(id, System.currentTimeMillis()))
        else dao.removeFavorite(id)
    }

    override suspend fun clearCache() = dao.clearCoins()
}
