package com.example.zaurscoin.domain.model

data class CoinList(
    val id: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val current_price: Double? = null,
    val image: String? = null,
    val price_change_percentage_24h: Double? = null,
    val market_cap_rank: Int? = null,
)
