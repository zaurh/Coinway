package com.zaurh.coinway.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CoinEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class CoinwayDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {
        const val NAME = "coinway.db"
    }
}
