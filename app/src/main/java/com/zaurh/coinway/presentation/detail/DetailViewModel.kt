package com.zaurh.coinway.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.MviViewModel
import com.zaurh.coinway.core.util.Formatters
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.core.util.toAppError
import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.FiatCurrency
import com.zaurh.coinway.domain.model.PricePoint
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.repository.SettingsRepository
import com.zaurh.coinway.presentation.navigation.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coinRepository: CoinRepository,
    private val settingsRepository: SettingsRepository,
) : MviViewModel<DetailState, DetailIntent, DetailEffect>(
    DetailState(coinId = savedStateHandle.toRoute<DetailRoute>().coinId)
) {

    private val coinId get() = currentState.coinId
    private var fiat: FiatCurrency = FiatCurrency.USD
    private val chartCache = mutableMapOf<ChartRange, List<PricePoint>>()
    private var chartJob: Job? = null

    init {
        viewModelScope.launch {
            fiat = settingsRepository.settings.first().currency
            setState { copy(currency = fiat.code) }
            launch { observeCachedCoin() }
            launch { coinRepository.observeIsFavorite(coinId).collect { setState { copy(isFavorite = it) } } }
            loadDetail()
            loadChart(currentState.range)
        }
    }

    override fun handleIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Retry -> viewModelScope.launch {
                if (currentState.detail == null) loadDetail()
                if (currentState.chart.isEmpty()) loadChart(currentState.range)
            }
            is DetailIntent.RangeSelected -> if (intent.range != currentState.range) loadChart(intent.range)
            is DetailIntent.Scrub -> setState { copy(scrubPoint = intent.point) }
            DetailIntent.ToggleFavorite -> toggleFavorite()
            DetailIntent.ToggleDescription -> setState { copy(isDescriptionExpanded = !isDescriptionExpanded) }
            DetailIntent.Share -> share()
            DetailIntent.OpenWebsite -> currentState.detail?.homepage?.let { sendEffect(DetailEffect.OpenUrl(it)) }
            DetailIntent.Back -> sendEffect(DetailEffect.NavigateBack)
        }
    }

    private suspend fun observeCachedCoin() {
        coinRepository.observeCoin(coinId).collect { coin ->
            if (coin == null) return@collect
            setState {
                val sameCurrency = coin.currency == fiat.code
                copy(
                    name = name.ifEmpty { coin.name },
                    symbol = symbol.ifEmpty { coin.symbol },
                    imageUrl = imageUrl.ifEmpty { coin.imageUrl },
                    rank = rank ?: coin.rank,
                    price = if (detail == null && sameCurrency) coin.price else price,
                    change24h = if (detail == null && sameCurrency) coin.change24h else change24h,
                )
            }
        }
    }

    private suspend fun loadDetail() {
        setState { copy(isLoading = true, error = null) }
        coinRepository.getCoinDetail(coinId, fiat)
            .onSuccess { detail ->
                setState {
                    copy(
                        detail = detail,
                        isLoading = false,
                        name = detail.name,
                        symbol = detail.symbol,
                        imageUrl = detail.imageUrl.ifEmpty { imageUrl },
                        rank = detail.rank ?: rank,
                        price = detail.price ?: price,
                        change24h = detail.change24h ?: change24h,
                    )
                }
            }
            .onFailure { setState { copy(isLoading = false, error = it.toAppError()) } }
    }

    private fun loadChart(range: ChartRange) {
        chartJob?.cancel()
        chartCache[range]?.let { cached ->
            setState { copy(range = range, chart = cached, isChartLoading = false, chartError = null, scrubPoint = null) }
            return
        }
        setState { copy(range = range, isChartLoading = true, chartError = null, scrubPoint = null) }
        chartJob = viewModelScope.launch {
            coinRepository.getPriceHistory(coinId, fiat, range)
                .onSuccess { points ->
                    chartCache[range] = points
                    setState { copy(chart = points, isChartLoading = false) }
                }
                .onFailure { setState { copy(chart = emptyList(), isChartLoading = false, chartError = it.toAppError()) } }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val nowFavorite = coinRepository.toggleFavorite(coinId)
            val message = if (nowFavorite) R.string.added_to_watchlist else R.string.removed_from_watchlist
            sendEffect(DetailEffect.ShowMessage(UiText.Res(message, currentState.name)))
        }
    }

    private fun share() {
        val state = currentState
        val price = state.price ?: return
        sendEffect(
            DetailEffect.ShareText(
                UiText.Res(
                    R.string.share_text,
                    state.name,
                    state.symbol.uppercase(),
                    Formatters.price(price, state.currency),
                    state.change24h?.let { Formatters.percent(it) } ?: "—",
                    PLAY_STORE_URL,
                )
            )
        )
    }

    companion object {
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.zaurh.coinway"
    }
}
