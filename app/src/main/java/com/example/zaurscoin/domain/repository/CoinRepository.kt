package com.example.zaurscoin.domain.repository

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoins(): Flow<Resource<List<CoinList>>>

    fun getCoin(id: String): Flow<Resource<Coin>>

    fun getChart(id: String): Flow<Resource<Chart>>

}