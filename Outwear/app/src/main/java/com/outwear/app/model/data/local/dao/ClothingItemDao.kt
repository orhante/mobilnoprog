package com.outwear.app.model.data.local.dao

import androidx.room.*
import com.outwear.app.model.data.local.entity.ClothingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {

    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    fun getItemById(id: Long): Flow<ClothingItemEntity?>

    @Query("SELECT * FROM clothing_items WHERE category = :category ORDER BY averageRating DESC")
    fun getItemsByCategory(category: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE brand = :brand ORDER BY averageRating DESC")
    fun getItemsByBrand(brand: String): Flow<List<ClothingItemEntity>>

    @Query("""
        SELECT * FROM clothing_items 
        WHERE name LIKE '%' || :query || '%' 
        OR brand LIKE '%' || :query || '%'
        OR category LIKE '%' || :query || '%'
        OR material LIKE '%' || :query || '%'
        ORDER BY averageRating DESC
    """)
    fun searchItems(query: String): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items ORDER BY averageRating DESC LIMIT :limit")
    fun getTopRatedItems(limit: Int = 10): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE isWishlisted = 1")
    fun getWishlistedItems(): Flow<List<ClothingItemEntity>>

    @Query("SELECT DISTINCT category FROM clothing_items ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClothingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ClothingItemEntity>)

    @Update
    suspend fun updateItem(item: ClothingItemEntity)

    @Query("UPDATE clothing_items SET isWishlisted = :isWishlisted WHERE id = :id")
    suspend fun updateWishlistStatus(id: Long, isWishlisted: Boolean)

    @Query("UPDATE clothing_items SET averageRating = :rating, reviewCount = :count WHERE id = :id")
    suspend fun updateRatingStats(id: Long, rating: Float, count: Int)

    @Delete
    suspend fun deleteItem(item: ClothingItemEntity)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT COUNT(*) FROM clothing_items")
    suspend fun getItemCount(): Int
}
