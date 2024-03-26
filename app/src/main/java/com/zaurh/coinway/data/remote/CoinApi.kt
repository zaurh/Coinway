package com.zaurh.coinway.data.remote

import com.zaurh.coinway.data.remote.dto.ChartDto
import com.zaurh.coinway.data.remote.dto.CoinDto
import com.zaurh.coinway.data.remote.dto.CoinListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {

    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") vs_currency: String = "usd",
        @Query("per_page") per_page: Int = 250
    ): List<CoinListDto>

    @GET("coins/{id}")
    suspend fun getCoin(
        @Path("id") id: String
    ): CoinDto

    @GET("coins/{id}/market_chart")
    suspend fun getChartsData(
        @Path("id") id: String,
        @Query("vs_currency") vs_currency: String = "usd",
        @Query("days") days: String = "1"
    ): ChartDto

}