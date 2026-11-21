package com.zaurh.coinway.presentation.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.CollectEffects
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.presentation.components.AutoRefreshEffect
import com.zaurh.coinway.presentation.components.ChangeBadge
import com.zaurh.coinway.presentation.components.CoinRow
import com.zaurh.coinway.presentation.components.MessageView
import com.zaurh.coinway.presentation.theme.CoinwayTheme

@Composable
fun WatchlistScreen(
    onOpenCoin: (String) -> Unit,
    onOpenMarkets: () -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val undoLabel = stringResource(R.string.undo)

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is WatchlistEffect.OpenCoin -> onOpenCoin(effect.coinId)
            WatchlistEffect.OpenMarkets -> onOpenMarkets()
            is WatchlistEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.text.asString(context))
            is WatchlistEffect.ShowUndo -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = effect.message.asString(context),
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onIntent(WatchlistIntent.UndoRemove(effect.coinId))
                }
            }
        }
    }
    AutoRefreshEffect { viewModel.onIntent(WatchlistIntent.AutoRefresh) }

    WatchlistContent(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistContent(
    state: WatchlistState,
    onIntent: (WatchlistIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.watchlist_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(WatchlistIntent.Refresh) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            if (!state.isLoading && state.coins.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    MessageView(
                        icon = Icons.Rounded.StarOutline,
                        title = stringResource(R.string.watchlist_empty_title),
                        body = stringResource(R.string.watchlist_empty_body),
                        actionLabel = stringResource(R.string.watchlist_browse),
                        onAction = { onIntent(WatchlistIntent.BrowseMarkets) },
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (state.coins.isNotEmpty()) {
                        item(key = "summary") { WatchlistSummary(state) }
                    }
                    items(state.coins, key = { it.id }) { coin ->
                        SwipeToRemoveRow(
                            coin = coin,
                            onClick = { onIntent(WatchlistIntent.CoinClicked(coin.id)) },
                            onRemove = { onIntent(WatchlistIntent.Remove(coin)) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                    item(key = "hint") {
                        Text(
                            text = stringResource(R.string.watchlist_swipe_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistSummary(state: WatchlistState) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.large)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.watchlist_today),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            ChangeBadge(change = state.averageChange, textStyle = MaterialTheme.typography.titleMedium)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = pluralStringResource(R.plurals.watchlist_coins, state.coins.size, state.coins.size),
                style = MaterialTheme.typography.titleMedium,
            )
            Row {
                Text(
                    text = stringResource(R.string.watchlist_up, state.gainers),
                    style = MaterialTheme.typography.labelMedium,
                    color = CoinwayTheme.marketColors.gain,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.watchlist_down, state.losers),
                    style = MaterialTheme.typography.labelMedium,
                    color = CoinwayTheme.marketColors.loss,
                )
            }
        }
    }
}

@Composable
private fun SwipeToRemoveRow(
    coin: Coin,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        onDismiss = { if (it == SwipeToDismissBoxValue.EndToStart) onRemove() },
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CoinwayTheme.marketColors.loss.copy(alpha = 0.85f))
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.remove_from_watchlist),
                    tint = Color.White,
                )
            }
        },
    ) {
        CoinRow(coin = coin, onClick = onClick)
    }
}
