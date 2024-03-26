package com.zaurh.coinway.domain.repository

import com.zaurh.coinway.common.Resource
import com.zaurh.coinway.domain.model.Chart
import com.zaurh.coinway.domain.model.Coin
import com.zaurh.coinway.domain.model.CoinList
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getCoins(): Flow<Resource<List<CoinList>>>

    fun getCoin(id: String): Flow<Resource<Coin>>

    fun getChart(id: String): Flow<Resource<Chart>>

}