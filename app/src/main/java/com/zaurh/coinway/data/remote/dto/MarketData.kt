package com.zaurh.coinway.data.remote.dto

data class MarketData(
    val current_price: CurrentPrice,
    val high_24h: High24h,
    val low_24h: Low24h,
    val market_cap: MarketCap,
    val market_cap_rank: Int,
    val total_volume: TotalVolume,
    val price_change_percentage_7d: Double,
    val price_change_percentage_14d: Double,
    val price_change_percentage_30d: Double,
    val price_change_percentage_1y: Double
)