package com.outwear.app.model.data.local.dao

import androidx.room.*
import com.outwear.app.model.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Query("SELECT * FROM wishlist_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getWishlistForUser(userId: Long): Flow<List<WishlistItemEntity>>

    @Query("SELECT clothingItemId FROM wishlist_items WHERE userId = :userId")
    fun getWishlistItemIds(userId: Long): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE userId = :userId AND clothingItemId = :itemId)")
    fun isItemWishlisted(userId: Long, itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId AND clothingItemId = :itemId")
    suspend fun removeFromWishlist(userId: Long, itemId: Long)

    @Query("DELETE FROM wishlist_items WHERE userId = :userId")
    suspend fun clearWishlist(userId: Long)

    @Query("SELECT COUNT(*) FROM wishlist_items WHERE userId = :userId")
    fun getWishlistCount(userId: Long): Flow<Int>
}
