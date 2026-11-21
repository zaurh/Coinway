package com.zaurh.coinway.presentation.watchlist

import com.zaurh.coinway.core.mvi.UiEffect
import com.zaurh.coinway.core.mvi.UiIntent
import com.zaurh.coinway.core.mvi.UiState
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.domain.model.Coin

data class WatchlistState(
    val coins: List<Coin> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
) : UiState {
    val gainers: Int get() = coins.count { it.change24h > 0 }
    val losers: Int get() = coins.count { it.change24h < 0 }
    val averageChange: Double? get() = coins.takeIf { it.isNotEmpty() }?.map { it.change24h }?.average()
}

sealed interface WatchlistIntent : UiIntent {
    data object Refresh : WatchlistIntent
    data object AutoRefresh : WatchlistIntent
    data class CoinClicked(val coinId: String) : WatchlistIntent
    data class Remove(val coin: Coin) : WatchlistIntent
    data class UndoRemove(val coinId: String) : WatchlistIntent
    data object BrowseMarkets : WatchlistIntent
}

sealed interface WatchlistEffect : UiEffect {
    data class OpenCoin(val coinId: String) : WatchlistEffect
    data object OpenMarkets : WatchlistEffect
    data class ShowUndo(val message: UiText, val coinId: String) : WatchlistEffect
    data class ShowMessage(val text: UiText) : WatchlistEffect
}
