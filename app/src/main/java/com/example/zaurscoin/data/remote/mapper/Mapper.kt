package com.example.zaurscoin.data.remote.mapper

import com.example.zaurscoin.data.local.entity.CoinListEntity
import com.example.zaurscoin.data.remote.dto.ChartDto
import com.example.zaurscoin.data.remote.dto.CoinDto
import com.example.zaurscoin.data.remote.dto.CoinListDto
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList

fun CoinListDto.toCoinList() =
    CoinList(id, name, symbol, current_price, image, price_change_percentage_24h)

fun CoinDto.toCoin() = Coin(id, image, name, symbol, market_data)

fun ChartDto.toChart() = Chart(prices)

fun CoinListDto.toCoinListEntity() =
    CoinListEntity(id, name, symbol, current_price, image, price_change_percentage_24h)

fun CoinListEntity.toCoinList() = CoinList(id, name, symbol, current_price, image, price_change_percentage_24h)