package com.zaurh.coinway.domain.model

data class PricePoint(val timestamp: Long, val price: Double)

enum class ChartRange(val label: String, val days: String) {
    Day("24H", "1"),
    Week("7D", "7"),
    Month("1M", "30"),
    Quarter("3M", "90"),
    Year("1Y", "365"),
}

data class GlobalMarket(
    val totalMarketCap: Double,
    val totalVolume: Double,
    val marketCapChange24h: Double,
    val btcDominance: Double,
    val currency: String,
)
