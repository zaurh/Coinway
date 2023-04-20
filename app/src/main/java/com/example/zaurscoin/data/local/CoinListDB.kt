package com.example.zaurscoin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zaurscoin.data.local.entity.CoinListEntity

@Database(entities = [CoinListEntity::class], version = 1)
abstract class CoinListDB: RoomDatabase() {
    abstract fun coinListDao():CoinListDao
}