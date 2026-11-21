package com.zaurh.coinway.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins")
data class CoinEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val marketCap: Double,
    val rank: Int?,
    val volume: Double,
    val change24h: Double,
    val sparkline: List<Double>,
    val currency: String,
    val updatedAt: Long,
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val addedAt: Long,
)

data class CoinWithFavorite(
    @Embedded val coin: CoinEntity,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean,
)
