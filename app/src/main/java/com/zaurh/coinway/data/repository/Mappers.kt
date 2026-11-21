package com.zaurh.coinway.data.repository

import androidx.core.text.HtmlCompat
import com.zaurh.coinway.data.local.CoinEntity
import com.zaurh.coinway.data.local.CoinWithFavorite
import com.zaurh.coinway.data.remote.dto.CoinDetailDto
import com.zaurh.coinway.data.remote.dto.GlobalDataDto
import com.zaurh.coinway.data.remote.dto.MarketChartDto
import com.zaurh.coinway.data.remote.dto.MarketCoinDto
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinDetail
import com.zaurh.coinway.domain.model.GlobalMarket
import com.zaurh.coinway.domain.model.PricePoint

private const val SPARKLINE_POINTS = 48

fun MarketCoinDto.toEntity(currency: String, now: Long) = CoinEntity(
    id = id,
    symbol = symbol,
    name = name,
    imageUrl = image.orEmpty(),
    price = currentPrice ?: 0.0,
    marketCap = marketCap ?: 0.0,
    rank = marketCapRank,
    volume = totalVolume ?: 0.0,
    change24h = priceChangePercentage24h ?: 0.0,
    sparkline = downsample(sparkline?.price.orEmpty().filterNotNull(), SPARKLINE_POINTS),
    currency = currency,
    updatedAt = now,
)

fun CoinWithFavorite.toDomain() = Coin(
    id = coin.id,
    symbol = coin.symbol,
    name = coin.name,
    imageUrl = coin.imageUrl,
    price = coin.price,
    marketCap = coin.marketCap,
    rank = coin.rank,
    volume = coin.volume,
    change24h = coin.change24h,
    sparkline = coin.sparkline,
    currency = coin.currency,
    updatedAt = coin.updatedAt,
    isFavorite = isFavorite,
)

fun CoinDetailDto.toDomain(currency: String): CoinDetail {
    val market = marketData
    return CoinDetail(
        id = id,
        symbol = symbol,
        name = name,
        imageUrl = image?.large ?: image?.small.orEmpty(),
        rank = marketCapRank,
        currency = currency,
        price = market?.currentPrice?.get(currency),
        marketCap = market?.marketCap?.get(currency),
        volume = market?.totalVolume?.get(currency),
        high24h = market?.high24h?.get(currency),
        low24h = market?.low24h?.get(currency),
        ath = market?.ath?.get(currency),
        athChangePercent = market?.athChangePercentage?.get(currency),
        atl = market?.atl?.get(currency),
        circulatingSupply = market?.circulatingSupply,
        maxSupply = market?.maxSupply,
        change24h = market?.change24h?.get(currency),
        change7d = market?.change7d?.get(currency),
        change14d = market?.change14d?.get(currency),
        change30d = market?.change30d?.get(currency),
        change1y = market?.change1y?.get(currency),
        description = description["en"].orEmpty().stripHtml(),
        homepage = links?.homepage?.firstOrNull { !it.isNullOrBlank() },
    )
}

fun MarketChartDto.toDomain(): List<PricePoint> = prices.mapNotNull { pair ->
    val time = pair.getOrNull(0) ?: return@mapNotNull null
    val price = pair.getOrNull(1) ?: return@mapNotNull null
    PricePoint(time.toLong(), price)
}

fun GlobalDataDto.toDomain(currency: String) = GlobalMarket(
    totalMarketCap = totalMarketCap[currency] ?: 0.0,
    totalVolume = totalVolume[currency] ?: 0.0,
    marketCapChange24h = marketCapChange24hUsd ?: 0.0,
    btcDominance = marketCapPercentage["btc"] ?: 0.0,
    currency = currency,
)

private fun String.stripHtml(): String =
    HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()

/** Keeps roughly [target] evenly spaced points, always including the last one. */
internal fun downsample(values: List<Double>, target: Int): List<Double> {
    if (values.size <= target) return values
    val step = (values.size - 1).toDouble() / (target - 1)
    return List(target) { index -> values[Math.round(index * step).toInt()] }
}
