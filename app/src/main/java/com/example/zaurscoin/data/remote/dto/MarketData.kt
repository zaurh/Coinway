package com.example.zaurscoin.data.remote.dto

data class MarketData(

    val current_price: CurrentPrice,
    val high_24h: High24h,
    val low_24h: Low24h,
    val market_cap: MarketCap
)