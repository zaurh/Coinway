package com.zaurh.coinway.domain.model

/** A coin as shown in market lists. Prices are expressed in [currency]. */
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val marketCap: Double,
    val rank: Int?,
    val volume: Double,
    val change24h: Double,
    /** Last 7 days of prices, oldest first, downsampled for drawing. */
    val sparkline: List<Double>,
    val currency: String,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
)
