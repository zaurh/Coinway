package com.zaurh.coinway.presentation.markets

import androidx.lifecycle.viewModelScope
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.MviViewModel
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.core.util.appErrorOrNull
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.repository.SettingsRepository
import com.zaurh.coinway.presentation.components.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketsViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val settingsRepository: SettingsRepository,
) : MviViewModel<MarketsState, MarketsIntent, MarketsEffect>(MarketsState()) {

    private var allCoins: List<Coin> = emptyList()
    private var currency: FiatCurrency = FiatCurrency.USD
    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            coinRepository.observeCoins().collect { coins ->
                allCoins = coins
                setState {
                    copy(
                        totalCount = coins.size,
                        lastUpdated = coins.maxOfOrNull { it.updatedAt },
                        isInitialLoading = isInitialLoading && coins.isEmpty(),
                    ).withVisibleCoins()
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.map { it.currency }.distinctUntilChanged().collect {
                currency = it
                refresh(userInitiated = false)
            }
        }
    }

    override fun handleIntent(intent: MarketsIntent) {
        when (intent) {
            is MarketsIntent.QueryChanged -> setState { copy(query = intent.query).withVisibleCoins() }
            MarketsIntent.ClearQuery -> setState { copy(query = "").withVisibleCoins() }
            is MarketsIntent.FilterSelected -> setState {
                val (sort, descending) = when (intent.filter) {
                    MarketFilter.All -> MarketSort.Rank to false
                    MarketFilter.Gainers -> MarketSort.Change to true
                    MarketFilter.Losers -> MarketSort.Change to false
                }
                copy(filter = intent.filter, sort = sort, sortDescending = descending).withVisibleCoins()
            }
            is MarketsIntent.SortSelected -> setState {
                val descending = if (sort == intent.sort) !sortDescending else intent.sort.defaultDescending
                copy(sort = intent.sort, sortDescending = descending).withVisibleCoins()
            }
            MarketsIntent.Refresh -> refresh(userInitiated = true)
            MarketsIntent.AutoRefresh -> if (refreshJob?.isActive != true) refresh(userInitiated = false)
            is MarketsIntent.CoinClicked -> sendEffect(MarketsEffect.OpenCoin(intent.coinId))
            is MarketsIntent.ToggleFavorite -> toggleFavorite(intent.coin)
        }
    }

    private fun refresh(userInitiated: Boolean) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            if (userInitiated) setState { copy(isRefreshing = true) }
            val requestedCurrency = currency
            val markets = async { coinRepository.refreshMarkets(requestedCurrency) }
            val global = async { coinRepository.getGlobalMarket(requestedCurrency) }
            val marketsResult = markets.await()
            val globalResult = global.await().getOrNull()
            val error = marketsResult.appErrorOrNull()
            setState {
                copy(
                    isRefreshing = false,
                    isInitialLoading = false,
                    error = error,
                    global = globalResult ?: this.global?.takeIf { it.currency == requestedCurrency.code },
                )
            }
            if (userInitiated && error != null && allCoins.isNotEmpty()) {
                sendEffect(MarketsEffect.ShowMessage(UiText.Res(error.messageRes())))
            }
        }
    }

    private fun toggleFavorite(coin: Coin) {
        viewModelScope.launch {
            val nowFavorite = coinRepository.toggleFavorite(coin.id)
            val message = if (nowFavorite) R.string.added_to_watchlist else R.string.removed_from_watchlist
            sendEffect(MarketsEffect.ShowMessage(UiText.Res(message, coin.name)))
        }
    }

    private fun MarketsState.withVisibleCoins(): MarketsState =
        copy(coins = applyQueryFilterSort(allCoins, query, filter, sort, sortDescending))
}

private val MarketSort.defaultDescending: Boolean
    get() = when (this) {
        MarketSort.Rank, MarketSort.Name -> false
        MarketSort.Price, MarketSort.Change -> true
    }

internal fun applyQueryFilterSort(
    coins: List<Coin>,
    query: String,
    filter: MarketFilter,
    sort: MarketSort,
    descending: Boolean,
): List<Coin> {
    val needle = query.trim()
    val filtered = coins.filter { coin ->
        val matchesQuery = needle.isEmpty() ||
            coin.name.contains(needle, ignoreCase = true) ||
            coin.symbol.contains(needle, ignoreCase = true)
        val matchesFilter = when (filter) {
            MarketFilter.All -> true
            MarketFilter.Gainers -> coin.change24h > 0
            MarketFilter.Losers -> coin.change24h < 0
        }
        matchesQuery && matchesFilter
    }
    val comparator: Comparator<Coin> = when (sort) {
        MarketSort.Rank -> compareBy(nullsLast()) { it.rank }
        MarketSort.Price -> compareBy { it.price }
        MarketSort.Change -> compareBy { it.change24h }
        MarketSort.Name -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
    }
    val sorted = filtered.sortedWith(if (descending) comparator.reversed() else comparator)
    // An exact symbol match ("eth") should win over coins that merely contain the text.
    if (needle.isEmpty()) return sorted
    val (exact, rest) = sorted.partition { it.symbol.equals(needle, ignoreCase = true) }
    return exact + rest
}
