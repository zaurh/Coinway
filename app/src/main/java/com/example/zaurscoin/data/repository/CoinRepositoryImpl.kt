package com.example.zaurscoin.data.repository

import com.example.zaurscoin.common.Resource
import com.example.zaurscoin.data.local.CoinListDao
import com.example.zaurscoin.data.remote.CoinApi
import com.example.zaurscoin.data.remote.mapper.toChart
import com.example.zaurscoin.data.remote.mapper.toCoin
import com.example.zaurscoin.data.remote.mapper.toCoinList
import com.example.zaurscoin.data.remote.mapper.toCoinListEntity
import com.example.zaurscoin.domain.model.Chart
import com.example.zaurscoin.domain.model.Coin
import com.example.zaurscoin.domain.model.CoinList
import com.example.zaurscoin.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinApi,
    private val dao: CoinListDao
): CoinRepository {

    override fun getCoins(): Flow<Resource<List<CoinList>>> = flow {

        emit(Resource.Loading())
        val localCoinList = dao.allLocalCoins().map { it.toCoinList() }
        emit(Resource.Loading(data = localCoinList))

        try {
            val remoteCoinList = api.getCoins()
            dao.deleteList()
            dao.insertList(remoteCoinList.map { it.toCoinListEntity() })
        }
        catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unexpected Error"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection"))
        }
        val newCoinList = dao.allLocalCoins().map { it.toCoinList() }
        emit(Resource.Success(newCoinList))
    }


    override fun getCoin(id:String): Flow<Resource<Coin>> = flow{
        try {
            emit(Resource.Loading())
            val coin = api.getCoin(id).toCoin()
            emit(Resource.Success(coin))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }


    }

    override fun getChart(id: String): Flow<Resource<Chart>> = flow {
        try {
            emit(Resource.Loading())
            val chart = api.getChartsData(id).toChart()
            emit(Resource.Success(chart))
        }
        catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

}