package com.example.zaurscoin.data.repository

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.data.remote.CoinApi
import com.example.zaurscoin.data.remote.mapper.toChart
import com.example.zaurscoin.data.remote.mapper.toCoin
import com.example.zaurscoin.data.remote.mapper.toCoinList
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinApi
): CoinRepository {

    override fun getCoins(): Flow<Resource<List<CoinList>>> = flow {
        emit(Resource.Loading())
        val coinList = api.getCoins().map { it.toCoinList() }
        emit(Resource.Success(coinList))
    }

    override fun getCoin(id:String): Flow<Resource<Coin>> = flow{
        emit(Resource.Loading())
        val coin = api.getCoin(id).toCoin()
        emit(Resource.Success(coin))
    }

    override fun getChart(id: String): Flow<Resource<Chart>> = flow {
        try {
            emit(Resource.Loading())
            val chart = api.getChartsData(id).toChart()
            emit(Resource.Success(chart))
        }
        catch (e: Exception) {
        }
    }

}