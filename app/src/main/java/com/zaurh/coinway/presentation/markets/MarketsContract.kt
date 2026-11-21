package com.zaurh.coinway.presentation.markets

import com.zaurh.coinway.core.mvi.UiEffect
import com.zaurh.coinway.core.mvi.UiIntent
import com.zaurh.coinway.core.mvi.UiState
import com.zaurh.coinway.core.util.AppError
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.GlobalMarket

enum class MarketFilter { All, Gainers, Losers }

enum class MarketSort { Rank, Price, Change, Name }

data class MarketsState(
    /** Coins after search, filter and sort are applied. */
    val coins: List<Coin> = emptyList(),
    /** Every cached coin, before search and filters. */
    val totalCount: Int = 0,
    val query: String = "",
    val filter: MarketFilter = MarketFilter.All,
    val sort: MarketSort = MarketSort.Rank,
    val sortDescending: Boolean = false,
    val global: GlobalMarket? = null,
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    /** Error of the latest refresh, if it failed. */
    val error: AppError? = null,
    val lastUpdated: Long? = null,
) : UiState {
    val showFullScreenError: Boolean get() = error != null && totalCount == 0 && !isInitialLoading
    val showStaleBanner: Boolean get() = error != null && totalCount > 0
    val showNoResults: Boolean get() = coins.isEmpty() && totalCount > 0
}

sealed interface MarketsIntent : UiIntent {
    data class QueryChanged(val query: String) : MarketsIntent
    data object ClearQuery : MarketsIntent
    data class FilterSelected(val filter: MarketFilter) : MarketsIntent
    /** Picking the active sort again flips its direction. */
    data class SortSelected(val sort: MarketSort) : MarketsIntent
    data object Refresh : MarketsIntent
    /** Periodic background refresh while the screen is visible. */
    data object AutoRefresh : MarketsIntent
    data class CoinClicked(val coinId: String) : MarketsIntent
    data class ToggleFavorite(val coin: Coin) : MarketsIntent
}

sealed interface MarketsEffect : UiEffect {
    data class OpenCoin(val coinId: String) : MarketsEffect
    data class ShowMessage(val text: UiText) : MarketsEffect
}
