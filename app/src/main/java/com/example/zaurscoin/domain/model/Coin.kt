package com.example.zaurscoin.domain.model

import com.example.zaurscoin.data.remote.dto.*

data class Coin(
    val id: String,
    val image: Image,
    val name: String,
    val symbol: String,
    val market_data: MarketData
)
