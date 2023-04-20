package com.example.zaurscoin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coinList")
data class CoinListEntity(
    val id: String,
    val name: String,
    val symbol: String,
    val current_price: Double,
    val image: String,
    val price_change_percentage_24h: Double,
    @PrimaryKey
    val roomId: Int? = null
)
