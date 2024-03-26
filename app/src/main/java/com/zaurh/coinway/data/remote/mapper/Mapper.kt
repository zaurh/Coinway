package com.zaurh.coinway.data.remote.mapper

import com.zaurh.coinway.data.local.entity.CoinListEntity
import com.zaurh.coinway.data.remote.dto.ChartDto
import com.zaurh.coinway.data.remote.dto.CoinDto
import com.zaurh.coinway.data.remote.dto.CoinListDto
import com.zaurh.coinway.domain.model.Chart
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinList

fun CoinDto.toCoin() = Coin(id, image, name, symbol, market_data)

fun ChartDto.toChart() = Chart(prices)

fun CoinListDto.toCoinListEntity() =
    CoinListEntity(
        id,
        name,
        symbol,
        current_price,
        image,
        price_change_percentage_24h,
        market_cap_rank
    )

fun CoinListEntity.toCoinList() =
    CoinList(id, name, symbol, current_price, image, price_change_percentage_24h, market_cap_rank)

