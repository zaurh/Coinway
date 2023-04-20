package com.example.zaurscoin.data.remote.dto

data class CoinDto(
    val id: String,
    val image: Image,
    val market_data: MarketData,
    val name: String,
    val symbol: String,
)