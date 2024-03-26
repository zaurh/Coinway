package com.zaurh.coinway.domain.model

import com.zaurh.coinway.data.remote.dto.Image
import com.zaurh.coinway.data.remote.dto.MarketData

data class Coin(
    val id: String,
    val image: Image,
    val name: String,
    val symbol: String,
    val market_data: MarketData
)
