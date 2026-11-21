package com.zaurh.coinway.domain.repository

import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.GlobalMarket
import com.zaurh.coinway.domain.model.PricePoint
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    /** Cached market list, ordered by rank. Emits again after every refresh. */
    fun observeCoins(): Flow<List<Coin>>

    /** Cached coins the person starred, most recently added first. */
    fun observeWatchlist(): Flow<List<Coin>>

    fun observeCoin(id: String): Flow<Coin?>

    fun observeIsFavorite(id: String): Flow<Boolean>

    /** Downloads the top coins and replaces the cache (starred coins are kept). */
    suspend fun refreshMarkets(currency: FiatCurrency): Result<Unit>

    /** Downloads fresh prices for every starred coin. */
    suspend fun refreshWatchlist(currency: FiatCurrency): Result<Unit>

    suspend fun getGlobalMarket(currency: FiatCurrency): Result<GlobalMarket>

    suspend fun getCoinDetail(id: String, currency: FiatCurrency): Result<CoinDetail>

    suspend fun getPriceHistory(id: String, currency: FiatCurrency, range: ChartRange): Result<List<PricePoint>>

    /** Stars or un-stars a coin and returns the new state. */
    suspend fun toggleFavorite(id: String): Boolean

    suspend fun setFavorite(id: String, favorite: Boolean)

    /** Removes cached prices but keeps the watchlist. */
    suspend fun clearCache()
}
