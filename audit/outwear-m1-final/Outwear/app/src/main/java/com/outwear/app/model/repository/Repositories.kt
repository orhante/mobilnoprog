package com.outwear.app.model.repository

import com.outwear.app.model.repository.mappers.*
import kotlinx.coroutines.flow.Flow

interface ClothingRepository {
    fun getAllItems(): Flow<List<ClothingItem>>
    fun getItemById(id: Long): Flow<ClothingItem?>
    fun getItemsByCategory(category: String): Flow<List<ClothingItem>>
    fun searchItems(query: String): Flow<List<ClothingItem>>
    fun getTopRatedItems(limit: Int = 10): Flow<List<ClothingItem>>
    fun getAllCategories(): Flow<List<String>>
    suspend fun toggleWishlist(itemId: Long, isWishlisted: Boolean)
    suspend fun insertItem(item: ClothingItem): Long
    suspend fun updateItem(item: ClothingItem)
    suspend fun deleteItem(id: Long)
    suspend fun seedIfEmpty()
}

interface ReviewRepository {
    fun getReviewsForItem(itemId: Long): Flow<List<Review>>
    fun getReviewsByUser(userId: Long): Flow<List<Review>>
    fun getRecentReviews(limit: Int): Flow<List<Review>>
    suspend fun addReview(review: Review): Long
    suspend fun updateReview(review: Review)
    suspend fun deleteReview(id: Long)
    suspend fun markHelpful(id: Long)
}

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    fun getUserById(id: Long): Flow<User?>
    suspend fun updateProfile(id: Long, name: String, bio: String, location: String)
    suspend fun seedCurrentUserIfNeeded()
}

interface BrandRepository {
    fun getAllBrands(): Flow<List<Brand>>
    fun getTopBrands(limit: Int): Flow<List<Brand>>
    suspend fun seedIfEmpty()
}

interface WishlistRepository {
    fun getWishlistIds(userId: Long): Flow<List<Long>>
    fun isItemWishlisted(userId: Long, itemId: Long): Flow<Boolean>
    suspend fun addToWishlist(userId: Long, itemId: Long)
    suspend fun removeFromWishlist(userId: Long, itemId: Long)
}
