package com.outwear.app.model.data.local.dao

import androidx.room.*
import com.outwear.app.model.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE clothingItemId = :itemId ORDER BY createdAt DESC")
    fun getReviewsForItem(itemId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReviewsByUser(userId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id = :id")
    fun getReviewById(id: Long): Flow<ReviewEntity?>

    @Query("SELECT AVG(rating) FROM reviews WHERE clothingItemId = :itemId")
    fun getAverageRating(itemId: Long): Flow<Float?>

    @Query("SELECT COUNT(*) FROM reviews WHERE clothingItemId = :itemId")
    fun getReviewCount(itemId: Long): Flow<Int>

    @Query("""
        SELECT * FROM reviews 
        WHERE clothingItemId = :itemId AND rating >= :minRating 
        ORDER BY createdAt DESC
    """)
    fun getReviewsFilteredByRating(itemId: Long, minRating: Float): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentReviews(limit: Int = 20): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY helpfulCount DESC LIMIT :limit")
    fun getMostHelpfulReviews(limit: Int = 10): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :id")
    suspend fun incrementHelpfulCount(id: Long)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReviewById(id: Long)

    @Query("DELETE FROM reviews WHERE clothingItemId = :itemId")
    suspend fun deleteAllReviewsForItem(itemId: Long)

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getReviewCount(): Int
}
