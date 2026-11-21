package com.zaurh.coinway.presentation.detail

import com.zaurh.coinway.core.mvi.UiEffect
import com.zaurh.coinway.core.mvi.UiIntent
import com.zaurh.coinway.core.mvi.UiState
import com.zaurh.coinway.core.util.AppError
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.PricePoint

data class DetailState(
    val coinId: String,
    // Header, filled instantly from the cache and then from the network.
    val name: String = "",
    val symbol: String = "",
    val imageUrl: String = "",
    val rank: Int? = null,
    val currency: String = "usd",
    val price: Double? = null,
    val change24h: Double? = null,
    val isFavorite: Boolean = false,
    // Full details
    val detail: CoinDetail? = null,
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val isDescriptionExpanded: Boolean = false,
    // Chart
    val range: ChartRange = ChartRange.Day,
    val chart: List<PricePoint> = emptyList(),
    val isChartLoading: Boolean = true,
    val chartError: AppError? = null,
    val scrubPoint: PricePoint? = null,
) : UiState {

    /** Change from the start of the chart to its end (or to the touched point). */
    val chartChange: Double?
        get() {
            val first = chart.firstOrNull()?.price ?: return null
            val last = (scrubPoint ?: chart.last()).price
            return if (first == 0.0) null else (last - first) / first * 100
        }

    val displayedPrice: Double? get() = scrubPoint?.price ?: price

    /** The official 24h figure for the 24H range, otherwise the change over the chart. */
    val displayedChange: Double?
        get() = when {
            scrubPoint == null && range == ChartRange.Day -> change24h ?: chartChange
            else -> chartChange
        }

    val isChartUp: Boolean
        get() = chart.size < 2 || chart.last().price >= chart.first().price

    val showFullScreenError: Boolean get() = error != null && detail == null && name.isEmpty()
}

sealed interface DetailIntent : UiIntent {
    data object Retry : DetailIntent
    data class RangeSelected(val range: ChartRange) : DetailIntent
    data class Scrub(val point: PricePoint?) : DetailIntent
    data object ToggleFavorite : DetailIntent
    data object ToggleDescription : DetailIntent
    data object Share : DetailIntent
    data object OpenWebsite : DetailIntent
    data object Back : DetailIntent
}

sealed interface DetailEffect : UiEffect {
    data class ShareText(val text: UiText) : DetailEffect
    data class OpenUrl(val url: String) : DetailEffect
    data class ShowMessage(val text: UiText) : DetailEffect
    data object NavigateBack : DetailEffect
}
