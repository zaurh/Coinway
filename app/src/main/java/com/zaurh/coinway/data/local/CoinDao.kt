package com.zaurh.coinway.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Query(
        """
        SELECT c.*, (f.id IS NOT NULL) AS isFavorite FROM coins c
        LEFT JOIN favorites f ON c.id = f.id
        ORDER BY c.rank IS NULL, c.rank, c.marketCap DESC
        """
    )
    fun observeCoins(): Flow<List<CoinWithFavorite>>

    @Query(
        """
        SELECT c.*, 1 AS isFavorite FROM coins c
        INNER JOIN favorites f ON c.id = f.id
        ORDER BY f.addedAt DESC
        """
    )
    fun observeWatchlist(): Flow<List<CoinWithFavorite>>

    @Query(
        """
        SELECT c.*, (f.id IS NOT NULL) AS isFavorite FROM coins c
        LEFT JOIN favorites f ON c.id = f.id
        WHERE c.id = :id
        """
    )
    fun observeCoin(id: String): Flow<CoinWithFavorite?>

    @Upsert
    suspend fun upsertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coins WHERE id NOT IN (:keepIds) AND id NOT IN (SELECT id FROM favorites)")
    suspend fun deleteCoinsExcept(keepIds: List<String>)

    @Transaction
    suspend fun replaceMarket(coins: List<CoinEntity>) {
        upsertCoins(coins)
        deleteCoinsExcept(coins.map { it.id })
    }

    @Query("DELETE FROM coins")
    suspend fun clearCoins()

    // Favorites

    @Query("SELECT id FROM favorites")
    suspend fun favoriteIds(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun observeIsFavorite(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun removeFavorite(id: String)
}
