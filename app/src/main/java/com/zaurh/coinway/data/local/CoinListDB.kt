package com.zaurh.coinway.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zaurh.coinway.data.local.entity.CoinListEntity

@Database(entities = [CoinListEntity::class], version = 7, exportSchema = false)
abstract class CoinListDB: RoomDatabase() {
    abstract fun coinListDao():CoinListDao
}