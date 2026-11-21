package com.zaurh.coinway.presentation.markets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.CollectEffects
import com.zaurh.coinway.core.util.Formatters
import com.zaurh.coinway.domain.model.GlobalMarket
import com.zaurh.coinway.presentation.components.AutoRefreshEffect
import com.zaurh.coinway.presentation.components.ChangeBadge
import com.zaurh.coinway.presentation.components.CoinRow
import com.zaurh.coinway.presentation.components.CoinRowPlaceholder
import com.zaurh.coinway.presentation.components.MessageView
import com.zaurh.coinway.presentation.components.messageRes
import com.zaurh.coinway.presentation.components.skeleton
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarketsScreen(
    onOpenCoin: (String) -> Unit,
    viewModel: MarketsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is MarketsEffect.OpenCoin -> onOpenCoin(effect.coinId)
            is MarketsEffect.ShowMessage -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(effect.text.asString(context))
            }
        }
    }
    AutoRefreshEffect { viewModel.onIntent(MarketsIntent.AutoRefresh) }

    MarketsContent(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketsContent(
    state: MarketsState,
    onIntent: (MarketsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 8 } }

    BackHandler(enabled = state.query.isNotEmpty()) {
        onIntent(MarketsIntent.ClearQuery)
        focusManager.clearFocus()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher2_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    SortMenu(
                        sort = state.sort,
                        descending = state.sortDescending,
                        onSelect = { onIntent(MarketsIntent.SortSelected(it)) },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(visible = showScrollToTop, enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()) {
                SmallFloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null)
                }
            }
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(MarketsIntent.Refresh) },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item(key = "overview") {
                    MarketOverviewCard(
                        global = state.global,
                        lastUpdated = state.lastUpdated,
                        isLoading = state.global == null && (state.isInitialLoading || state.isRefreshing),
                    )
                }
                if (state.showStaleBanner) {
                    item(key = "stale") {
                        StaleBanner(onRetry = { onIntent(MarketsIntent.Refresh) })
                    }
                }
                stickyHeader(key = "controls") {
                    SearchAndFilters(
                        query = state.query,
                        filter = state.filter,
                        onQueryChange = { onIntent(MarketsIntent.QueryChanged(it)) },
                        onClear = { onIntent(MarketsIntent.ClearQuery) },
                        onFilter = { onIntent(MarketsIntent.FilterSelected(it)) },
                    )
                }
                when {
                    state.isInitialLoading && state.totalCount == 0 ->
                        items(10, key = { "placeholder$it" }) { CoinRowPlaceholder() }

                    state.showFullScreenError -> item(key = "error") {
                        MessageView(
                            icon = Icons.Rounded.CloudOff,
                            title = stringResource(R.string.error_title),
                            body = stringResource(state.error!!.messageRes()),
                            actionLabel = stringResource(R.string.retry),
                            onAction = { onIntent(MarketsIntent.Refresh) },
                        )
                    }

                    state.showNoResults -> item(key = "empty") {
                        MessageView(
                            icon = Icons.Rounded.SearchOff,
                            title = stringResource(R.string.no_results_title),
                            body = stringResource(R.string.no_results_body, state.query.trim()),
                        )
                    }

                    else -> items(state.coins, key = { it.id }) { coin ->
                        CoinRow(
                            coin = coin,
                            onClick = {
                                focusManager.clearFocus()
                                onIntent(MarketsIntent.CoinClicked(coin.id))
                            },
                            onLongClick = { onIntent(MarketsIntent.ToggleFavorite(coin)) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketOverviewCard(global: GlobalMarket?, lastUpdated: Long?, isLoading: Boolean) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(colors.primary.copy(alpha = 0.22f), colors.surfaceContainerHigh, colors.surfaceContainerHigh)
                ),
                shape = MaterialTheme.shapes.large,
            )
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.global_market_cap),
            style = MaterialTheme.typography.labelLarge,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        if (global == null) {
            if (isLoading) {
                Box(Modifier.width(180.dp).height(32.dp).skeleton())
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth().height(36.dp).skeleton())
            } else {
                Text("—", style = MaterialTheme.typography.headlineLarge)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = Formatters.compactPrice(global.totalMarketCap, global.currency),
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(Modifier.width(10.dp))
                ChangeBadge(change = global.marketCapChange24h)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                OverviewStat(
                    label = stringResource(R.string.global_volume),
                    value = Formatters.compactPrice(global.totalVolume, global.currency),
                )
                OverviewStat(
                    label = stringResource(R.string.global_btc_dominance),
                    value = String.format(Locale.getDefault(), "%.1f%%", global.btcDominance),
                )
            }
        }
        if (lastUpdated != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(
                    R.string.updated_at,
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(lastUpdated)),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OverviewStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun StaleBanner(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), MaterialTheme.shapes.medium)
            .clickable(onClick = onRetry)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.offline_banner),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = stringResource(R.string.retry),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SearchAndFilters(
    query: String,
    filter: MarketFilter,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onFilter: (MarketFilter) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp, bottom = 4.dp),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.clear_search))
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            ),
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MarketFilter.entries.forEach { option ->
                FilterChip(
                    selected = option == filter,
                    onClick = { onFilter(option) },
                    label = {
                        Text(
                            stringResource(
                                when (option) {
                                    MarketFilter.All -> R.string.filter_all
                                    MarketFilter.Gainers -> R.string.filter_gainers
                                    MarketFilter.Losers -> R.string.filter_losers
                                }
                            )
                        )
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = option == filter,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SortMenu(sort: MarketSort, descending: Boolean, onSelect: (MarketSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = stringResource(R.string.sort_by))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            MarketSort.entries.forEach { option ->
                val selected = option == sort
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                when (option) {
                                    MarketSort.Rank -> R.string.sort_rank
                                    MarketSort.Price -> R.string.sort_price
                                    MarketSort.Change -> R.string.sort_change
                                    MarketSort.Name -> R.string.sort_name
                                }
                            ),
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    trailingIcon = {
                        if (selected) {
                            Icon(
                                imageVector = if (descending) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
