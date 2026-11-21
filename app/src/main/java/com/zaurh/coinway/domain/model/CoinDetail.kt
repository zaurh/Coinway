package com.zaurh.coinway.domain.model

data class CoinDetail(
    val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val rank: Int?,
    val currency: String,
    val price: Double?,
    val marketCap: Double?,
    val volume: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val ath: Double?,
    val athChangePercent: Double?,
    val atl: Double?,
    val circulatingSupply: Double?,
    val maxSupply: Double?,
    val change24h: Double?,
    val change7d: Double?,
    val change14d: Double?,
    val change30d: Double?,
    val change1y: Double?,
    /** Plain-text description (HTML stripped). */
    val description: String,
    val homepage: String?,
)
