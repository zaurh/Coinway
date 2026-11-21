package com.zaurh.coinway.presentation.watchlist

import androidx.lifecycle.viewModelScope
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.MviViewModel
import com.zaurh.coinway.core.util.UiText
import com.zaurh.coinway.core.util.appErrorOrNull
import com.zaurh.coinway.domain.repository.CoinRepository
import com.zaurh.coinway.domain.repository.SettingsRepository
import com.zaurh.coinway.presentation.components.messageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val settingsRepository: SettingsRepository,
) : MviViewModel<WatchlistState, WatchlistIntent, WatchlistEffect>(WatchlistState()) {

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            coinRepository.observeWatchlist().collect { coins ->
                setState { copy(coins = coins, isLoading = false) }
            }
        }
        refresh(userInitiated = false)
    }

    override fun handleIntent(intent: WatchlistIntent) {
        when (intent) {
            WatchlistIntent.Refresh -> refresh(userInitiated = true)
            WatchlistIntent.AutoRefresh -> if (refreshJob?.isActive != true) refresh(userInitiated = false)
            is WatchlistIntent.CoinClicked -> sendEffect(WatchlistEffect.OpenCoin(intent.coinId))
            is WatchlistIntent.Remove -> viewModelScope.launch {
                coinRepository.setFavorite(intent.coin.id, favorite = false)
                sendEffect(
                    WatchlistEffect.ShowUndo(
                        message = UiText.Res(R.string.removed_from_watchlist, intent.coin.name),
                        coinId = intent.coin.id,
                    )
                )
            }
            is WatchlistIntent.UndoRemove -> viewModelScope.launch {
                coinRepository.setFavorite(intent.coinId, favorite = true)
            }
            WatchlistIntent.BrowseMarkets -> sendEffect(WatchlistEffect.OpenMarkets)
        }
    }

    private fun refresh(userInitiated: Boolean) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            if (userInitiated) setState { copy(isRefreshing = true) }
            val currency = settingsRepository.settings.first().currency
            val error = coinRepository.refreshWatchlist(currency).appErrorOrNull()
            setState { copy(isRefreshing = false) }
            if (userInitiated && error != null) {
                sendEffect(WatchlistEffect.ShowMessage(UiText.Res(error.messageRes())))
            }
        }
    }
}
