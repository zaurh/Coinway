package com.example.zaurscoin.data.local

import androidx.room.*
import com.example.zaurscoin.data.local.entity.CoinListEntity

@Dao
interface CoinListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(coinListEntity: List<CoinListEntity>)

    @Query("delete from coinList")
    suspend fun deleteList()

    @Query("SELECT * FROM coinList")
    suspend fun allLocalCoins():List<CoinListEntity>

}