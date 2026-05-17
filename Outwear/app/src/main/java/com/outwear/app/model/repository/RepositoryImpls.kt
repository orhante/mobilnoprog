package com.outwear.app.model.repository

import com.outwear.app.model.data.local.dao.*
import com.outwear.app.model.data.local.entity.WishlistItemEntity
import com.outwear.app.model.data.local.util.DatabaseSeeder
import com.outwear.app.model.repository.mappers.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClothingRepositoryImpl @Inject constructor(
    private val clothingItemDao: ClothingItemDao
) : ClothingRepository {

    override fun getAllItems(): Flow<List<ClothingItem>> =
        clothingItemDao.getAllItems().map { it.map { e -> e.toDomain() } }

    override fun getItemById(id: Long): Flow<ClothingItem?> =
        clothingItemDao.getItemById(id).map { it?.toDomain() }

    override fun getItemsByCategory(category: String): Flow<List<ClothingItem>> =
        clothingItemDao.getItemsByCategory(category).map { it.map { e -> e.toDomain() } }

    override fun searchItems(query: String): Flow<List<ClothingItem>> =
        clothingItemDao.searchItems(query).map { it.map { e -> e.toDomain() } }

    override fun getTopRatedItems(limit: Int): Flow<List<ClothingItem>> =
        clothingItemDao.getTopRatedItems(limit).map { it.map { e -> e.toDomain() } }

    override fun getAllCategories(): Flow<List<String>> =
        clothingItemDao.getAllCategories()

    override suspend fun toggleWishlist(itemId: Long, isWishlisted: Boolean) {
        clothingItemDao.updateWishlistStatus(itemId, isWishlisted)
    }

    override suspend fun insertItem(item: ClothingItem): Long =
        clothingItemDao.insertItem(item.toEntity())

    override suspend fun updateItem(item: ClothingItem) =
        clothingItemDao.updateItem(item.toEntity())

    override suspend fun deleteItem(id: Long) =
        clothingItemDao.deleteItemById(id)

    override suspend fun seedIfEmpty() {
        if (clothingItemDao.getItemCount() == 0) {
            clothingItemDao.insertItems(DatabaseSeeder.getSeedClothingItems())
        }
    }
}

class ReviewRepositoryImpl @Inject constructor(
    private val reviewDao: ReviewDao,
    private val userDao: UserDao
) : ReviewRepository {

    override fun getReviewsForItem(itemId: Long): Flow<List<Review>> =
        reviewDao.getReviewsForItem(itemId).map { reviews ->
            reviews.map { r ->
                val author = userDao.getCurrentUser()
                r.toDomain()
            }
        }

    override fun getReviewsByUser(userId: Long): Flow<List<Review>> =
        reviewDao.getReviewsByUser(userId).map { it.map { r -> r.toDomain() } }

    override fun getRecentReviews(limit: Int): Flow<List<Review>> =
        reviewDao.getRecentReviews(limit).map { it.map { r -> r.toDomain() } }

    override suspend fun addReview(review: Review): Long {
        val id = reviewDao.insertReview(review.toEntity())
        return id
    }

    override suspend fun updateReview(review: Review) =
        reviewDao.updateReview(review.toEntity())

    override suspend fun deleteReview(id: Long) =
        reviewDao.deleteReviewById(id)

    override suspend fun markHelpful(id: Long) =
        reviewDao.incrementHelpfulCount(id)
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getCurrentUser(): Flow<User?> =
        userDao.getCurrentUser().map { it?.toDomain() }

    override fun getUserById(id: Long): Flow<User?> =
        userDao.getUserById(id).map { it?.toDomain() }

    override suspend fun updateProfile(id: Long, name: String, bio: String, location: String) =
        userDao.updateProfile(id, name, bio, location)

    override suspend fun seedCurrentUserIfNeeded() {
        if (userDao.getUserCount() == 0) {
            userDao.insertUser(DatabaseSeeder.getSeedUsers().first())
            DatabaseSeeder.getSeedUsers().drop(1).forEach { userDao.insertUser(it) }
        }
    }
}

class BrandRepositoryImpl @Inject constructor(
    private val brandDao: BrandDao
) : BrandRepository {

    override fun getAllBrands(): Flow<List<Brand>> =
        brandDao.getAllBrands().map { it.map { e -> e.toDomain() } }

    override fun getTopBrands(limit: Int): Flow<List<Brand>> =
        brandDao.getTopBrands(limit).map { it.map { e -> e.toDomain() } }

    override suspend fun seedIfEmpty() {
        if (brandDao.getBrandCount() == 0) {
            brandDao.insertBrands(DatabaseSeeder.getSeedBrands())
        }
    }
}

class WishlistRepositoryImpl @Inject constructor(
    private val wishlistDao: WishlistDao
) : WishlistRepository {

    override fun getWishlistIds(userId: Long): Flow<List<Long>> =
        wishlistDao.getWishlistItemIds(userId)

    override fun isItemWishlisted(userId: Long, itemId: Long): Flow<Boolean> =
        wishlistDao.isItemWishlisted(userId, itemId)

    override suspend fun addToWishlist(userId: Long, itemId: Long) =
        wishlistDao.addToWishlist(WishlistItemEntity(userId = userId, clothingItemId = itemId))

    override suspend fun removeFromWishlist(userId: Long, itemId: Long) =
        wishlistDao.removeFromWishlist(userId, itemId)
}
