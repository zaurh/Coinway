package com.example.zaurscoin.data.local

import androidx.room.*
import com.example.zaurscoin.data.local.entity.CoinListEntity
import javax.inject.Inject

@Dao
interface CoinListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(coinListEntity: List<CoinListEntity>)

    @Delete
    suspend fun delete(coinListEntity: CoinListEntity)

    @Query("delete from coinList")
    suspend fun deleteList()

    @Query("SELECT * FROM coinList")
    suspend fun allLocalCoins():List<CoinListEntity>

}