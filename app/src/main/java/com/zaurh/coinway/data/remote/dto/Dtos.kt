package com.zaurh.coinway.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// CoinGecko returns null for many fields of smaller coins, so everything is nullable.

@Serializable
data class MarketCoinDto(
    val id: String,
    val symbol: String = "",
    val name: String = "",
    val image: String? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("market_cap") val marketCap: Double? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("total_volume") val totalVolume: Double? = null,
    @SerialName("price_change_percentage_24h") val priceChangePercentage24h: Double? = null,
    @SerialName("sparkline_in_7d") val sparkline: SparklineDto? = null,
)

@Serializable
data class SparklineDto(val price: List<Double?> = emptyList())

@Serializable
data class CoinDetailDto(
    val id: String,
    val symbol: String = "",
    val name: String = "",
    val image: ImageDto? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("market_data") val marketData: MarketDataDto? = null,
    val description: Map<String, String?> = emptyMap(),
    val links: LinksDto? = null,
)

@Serializable
data class ImageDto(
    val thumb: String? = null,
    val small: String? = null,
    val large: String? = null,
)

@Serializable
data class LinksDto(val homepage: List<String?> = emptyList())

@Serializable
data class MarketDataDto(
    @SerialName("current_price") val currentPrice: Map<String, Double?> = emptyMap(),
    @SerialName("market_cap") val marketCap: Map<String, Double?> = emptyMap(),
    @SerialName("total_volume") val totalVolume: Map<String, Double?> = emptyMap(),
    @SerialName("high_24h") val high24h: Map<String, Double?> = emptyMap(),
    @SerialName("low_24h") val low24h: Map<String, Double?> = emptyMap(),
    val ath: Map<String, Double?> = emptyMap(),
    @SerialName("ath_change_percentage") val athChangePercentage: Map<String, Double?> = emptyMap(),
    val atl: Map<String, Double?> = emptyMap(),
    @SerialName("price_change_percentage_24h_in_currency") val change24h: Map<String, Double?> = emptyMap(),
    @SerialName("price_change_percentage_7d_in_currency") val change7d: Map<String, Double?> = emptyMap(),
    @SerialName("price_change_percentage_14d_in_currency") val change14d: Map<String, Double?> = emptyMap(),
    @SerialName("price_change_percentage_30d_in_currency") val change30d: Map<String, Double?> = emptyMap(),
    @SerialName("price_change_percentage_1y_in_currency") val change1y: Map<String, Double?> = emptyMap(),
    @SerialName("circulating_supply") val circulatingSupply: Double? = null,
    @SerialName("max_supply") val maxSupply: Double? = null,
)

@Serializable
data class MarketChartDto(val prices: List<List<Double?>> = emptyList())

@Serializable
data class GlobalResponseDto(val data: GlobalDataDto)

@Serializable
data class GlobalDataDto(
    @SerialName("total_market_cap") val totalMarketCap: Map<String, Double?> = emptyMap(),
    @SerialName("total_volume") val totalVolume: Map<String, Double?> = emptyMap(),
    @SerialName("market_cap_percentage") val marketCapPercentage: Map<String, Double?> = emptyMap(),
    @SerialName("market_cap_change_percentage_24h_usd") val marketCapChange24hUsd: Double? = null,
)
