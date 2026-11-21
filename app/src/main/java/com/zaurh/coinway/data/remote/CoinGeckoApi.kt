package com.zaurh.coinway.data.remote

import com.zaurh.coinway.data.remote.dto.CoinDetailDto
import com.zaurh.coinway.data.remote.dto.GlobalResponseDto
import com.zaurh.coinway.data.remote.dto.MarketChartDto
import com.zaurh.coinway.data.remote.dto.MarketCoinDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String,
        @Query("ids") ids: String? = null,
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 250,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true,
        @Query("price_change_percentage") priceChangePercentage: String = "24h",
    ): List<MarketCoinDto>

    @GET("coins/{id}")
    suspend fun getCoin(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false,
    ): CoinDetailDto

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") currency: String,
        @Query("days") days: String,
    ): MarketChartDto

    @GET("global")
    suspend fun getGlobal(): GlobalResponseDto

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}
