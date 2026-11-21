package com.zaurh.coinway.presentation.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaurh.coinway.R
import com.zaurh.coinway.core.mvi.CollectEffects
import com.zaurh.coinway.core.util.Formatters
import com.zaurh.coinway.domain.model.ChartRange
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.PricePoint
import com.zaurh.coinway.presentation.components.ChangeBadge
import com.zaurh.coinway.presentation.components.CoinAvatar
import com.zaurh.coinway.presentation.components.MessageView
import com.zaurh.coinway.presentation.components.PriceChart
import com.zaurh.coinway.presentation.components.messageRes
import com.zaurh.coinway.presentation.components.skeleton
import com.zaurh.coinway.presentation.theme.CoinwayTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            DetailEffect.NavigateBack -> onBack()
            is DetailEffect.ShowMessage -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(effect.text.asString(context))
            }
            is DetailEffect.OpenUrl -> try {
                context.startActivity(Intent(Intent.ACTION_VIEW, effect.url.toUri()))
            } catch (_: ActivityNotFoundException) {
            }
            is DetailEffect.ShareText -> {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, effect.text.asString(context))
                }
                context.startActivity(Intent.createChooser(send, null))
            }
        }
    }

    DetailContent(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onIntent(DetailIntent.Back) }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                title = {
                    Text(
                        text = state.symbol.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                actions = {
                    FavoriteButton(isFavorite = state.isFavorite, onClick = { onIntent(DetailIntent.ToggleFavorite) })
                    IconButton(onClick = { onIntent(DetailIntent.Share) }, enabled = state.price != null) {
                        Icon(Icons.Rounded.Share, contentDescription = stringResource(R.string.share))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.showFullScreenError) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MessageView(
                    icon = Icons.Rounded.CloudOff,
                    title = stringResource(R.string.error_title),
                    body = stringResource(state.error!!.messageRes()),
                    actionLabel = stringResource(R.string.retry),
                    onAction = { onIntent(DetailIntent.Retry) },
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Header(state)
            Spacer(Modifier.height(8.dp))
            ChartSection(state, onIntent)
            Spacer(Modifier.height(8.dp))

            val detail = state.detail
            when {
                detail != null -> {
                    if (detail.low24h != null && detail.high24h != null && state.price != null) {
                        DayRangeCard(low = detail.low24h, high = detail.high24h, price = state.price, currency = detail.currency)
                    }
                    PerformanceCard(detail)
                    StatisticsCard(detail)
                    if (detail.description.isNotBlank() || detail.homepage != null) {
                        AboutCard(
                            detail = detail,
                            expanded = state.isDescriptionExpanded,
                            onToggle = { onIntent(DetailIntent.ToggleDescription) },
                            onOpenWebsite = { onIntent(DetailIntent.OpenWebsite) },
                        )
                    }
                }
                state.isLoading -> StatsPlaceholder()
                state.error != null -> MessageView(
                    icon = Icons.Rounded.CloudOff,
                    title = stringResource(R.string.error_title),
                    body = stringResource(state.error.messageRes()),
                    actionLabel = stringResource(R.string.retry),
                    onAction = { onIntent(DetailIntent.Retry) },
                )
            }

            Text(
                text = stringResource(R.string.data_attribution),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
            )
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "favoriteScale",
    )
    val tint by animateColorAsState(
        targetValue = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        label = "favoriteTint",
    )
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarOutline,
            contentDescription = stringResource(
                if (isFavorite) R.string.remove_from_watchlist else R.string.add_to_watchlist
            ),
            tint = tint,
            modifier = Modifier.scale(scale),
        )
    }
}

@Composable
private fun Header(state: DetailState) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.name.isEmpty()) {
                Box(Modifier.size(44.dp).skeleton(CircleShape))
                Spacer(Modifier.width(12.dp))
                Box(Modifier.width(140.dp).height(22.dp).skeleton())
            } else {
                CoinAvatar(imageUrl = state.imageUrl, name = state.name, size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                state.rank?.let { rank ->
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.rank_format, rank),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val price = state.displayedPrice
        if (price == null) {
            Box(Modifier.width(200.dp).height(40.dp).skeleton())
        } else {
            Text(
                text = Formatters.price(price, state.currency),
                style = MaterialTheme.typography.displaySmall,
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ChangeBadge(change = state.displayedChange)
            Spacer(Modifier.width(8.dp))
            AnimatedContent(
                targetState = state.scrubPoint?.let { formatPointTime(it, state.range) }
                    ?: stringResource(state.range.captionRes()),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "chartCaption",
            ) { caption ->
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ChartSection(state: DetailState, onIntent: (DetailIntent) -> Unit) {
    val colors = CoinwayTheme.marketColors
    val lineColor by animateColorAsState(
        targetValue = if (state.isChartUp) colors.gain else colors.loss,
        label = "chartColor",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.chart.size >= 2 -> PriceChart(
                points = state.chart,
                lineColor = lineColor,
                guideColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onScrub = { onIntent(DetailIntent.Scrub(it)) },
                modifier = Modifier.fillMaxSize(),
            )
            state.isChartLoading -> CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp),
            )
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.chart_error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { onIntent(DetailIntent.Retry) }) { Text(stringResource(R.string.retry)) }
            }
        }
        if (state.isChartLoading && state.chart.size >= 2) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp),
            )
        }
    }
    RangeSelector(selected = state.range, onSelect = { onIntent(DetailIntent.RangeSelected(it)) })
}

@Composable
private fun RangeSelector(selected: ChartRange, onSelect: (ChartRange) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.medium)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ChartRange.entries.forEach { range ->
            val isSelected = range == selected
            val background by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                label = "rangeBg",
            )
            val content by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "rangeFg",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(background, MaterialTheme.shapes.small)
                    .clickable { onSelect(range) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(range.label, style = MaterialTheme.typography.labelLarge, color = content)
            }
        }
    }
}

@Composable
private fun SectionCard(title: String?, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.large)
            .padding(20.dp),
    ) {
        if (title != null) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
        }
        content()
    }
}

@Composable
private fun DayRangeCard(low: Double, high: Double, price: Double, currency: String) {
    val fraction = if (high > low) ((price - low) / (high - low)).toFloat().coerceIn(0f, 1f) else 0.5f
    val colors = CoinwayTheme.marketColors
    SectionCard(title = stringResource(R.string.day_range)) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(colors.loss, colors.gain)),
                        CircleShape,
                    )
            )
            Box(
                Modifier
                    .offset(x = (maxWidth - 14.dp) * fraction)
                    .size(14.dp)
                    .background(MaterialTheme.colorScheme.onSurface, CircleShape)
                    .padding(3.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LabeledValue(stringResource(R.string.stat_low), Formatters.price(low, currency))
            LabeledValue(stringResource(R.string.stat_high), Formatters.price(high, currency), alignEnd = true)
        }
    }
}

@Composable
private fun PerformanceCard(detail: CoinDetail) {
    val items = listOf(
        R.string.perf_24h_short to detail.change24h,
        R.string.perf_7d_short to detail.change7d,
        R.string.perf_30d_short to detail.change30d,
        R.string.perf_1y_short to detail.change1y,
    )
    SectionCard(title = stringResource(R.string.performance)) {
        Row(Modifier.fillMaxWidth()) {
            items.forEach { (label, change) ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = change?.let { Formatters.percent(it) } ?: stringResource(R.string.not_available),
                        style = MaterialTheme.typography.labelLarge,
                        color = CoinwayTheme.marketColors.forChange(change),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsCard(detail: CoinDetail) {
    val na = stringResource(R.string.not_available)
    val currency = detail.currency
    fun money(value: Double?) = value?.let { Formatters.price(it, currency) } ?: na
    fun compactMoney(value: Double?) = value?.takeIf { it > 0 }?.let { Formatters.compactPrice(it, currency) } ?: na
    fun supply(value: Double?) = value?.takeIf { it > 0 }?.let { Formatters.compactNumber(it) } ?: "∞"

    val rows = listOf(
        Triple(R.string.stat_market_cap, compactMoney(detail.marketCap), null),
        Triple(R.string.stat_volume, compactMoney(detail.volume), null),
        Triple(
            R.string.stat_ath,
            money(detail.ath),
            detail.athChangePercent?.let { stringResource(R.string.from_ath, Formatters.percent(it)) },
        ),
        Triple(R.string.stat_atl, money(detail.atl), null),
        Triple(R.string.stat_circulating, detail.circulatingSupply?.let { Formatters.compactNumber(it) } ?: na, null),
        Triple(R.string.stat_max_supply, supply(detail.maxSupply), null),
    )
    SectionCard(title = stringResource(R.string.statistics)) {
        rows.chunked(2).forEachIndexed { index, pair ->
            if (index > 0) Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth()) {
                pair.forEach { (label, value, caption) ->
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = stringResource(label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (caption != null) {
                            Text(
                                text = caption,
                                style = MaterialTheme.typography.labelSmall,
                                color = CoinwayTheme.marketColors.forChange(detail.athChangePercent),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutCard(
    detail: CoinDetail,
    expanded: Boolean,
    onToggle: () -> Unit,
    onOpenWebsite: () -> Unit,
) {
    SectionCard(title = stringResource(R.string.about_coin, detail.name)) {
        if (detail.description.isNotBlank()) {
            Text(
                text = detail.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .animateContentSize()
                    .clickable(onClick = onToggle),
            )
            TextButton(onClick = onToggle, modifier = Modifier.offset(x = (-12).dp)) {
                Text(stringResource(if (expanded) R.string.show_less else R.string.read_more))
            }
        }
        if (detail.homepage != null) {
            OutlinedButton(onClick = onOpenWebsite) {
                Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.website))
            }
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String, alignEnd: Boolean = false) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun StatsPlaceholder() {
    repeat(2) {
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(140.dp)
                .skeleton(MaterialTheme.shapes.large)
        ) {}
    }
}

private fun ChartRange.captionRes(): Int = when (this) {
    ChartRange.Day -> R.string.range_day
    ChartRange.Week -> R.string.range_week
    ChartRange.Month -> R.string.range_month
    ChartRange.Quarter -> R.string.range_quarter
    ChartRange.Year -> R.string.range_year
}

private fun formatPointTime(point: PricePoint, range: ChartRange): String {
    val skeleton = when (range) {
        ChartRange.Day -> "jm"
        ChartRange.Week -> "MMMdjm"
        ChartRange.Month, ChartRange.Quarter -> "MMMd"
        ChartRange.Year -> "yMMMd"
    }
    val locale = Locale.getDefault()
    val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
    return SimpleDateFormat(pattern, locale).format(Date(point.timestamp))
}
