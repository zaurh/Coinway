package com.example.zaurscoin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zaurscoin.data.local.entity.CoinListEntity

@Database(entities = [CoinListEntity::class], version = 7)
abstract class CoinListDB: RoomDatabase() {
    abstract fun coinListDao():CoinListDao
}