package com.outwear.app.model.repository.mappers

import com.outwear.app.model.data.local.entity.*

// --- Domain models (what the UI sees) ---

data class ClothingItem(
    val id: Long,
    val name: String,
    val brand: String,
    val category: String,
    val material: String,
    val description: String,
    val imageUrl: String,
    val priceRange: String,
    val averageRating: Float,
    val reviewCount: Int,
    val weatherSuitability: String,
    val createdAt: Long,
    val isWishlisted: Boolean
)

data class Review(
    val id: Long,
    val clothingItemId: Long,
    val userId: Long,
    val rating: Float,
    val title: String,
    val body: String,
    val pros: List<String>,
    val cons: List<String>,
    val weatherCondition: String,
    val fitRating: Int,
    val warmthRating: Int,
    val durabilityRating: Int,
    val valueRating: Int,
    val helpfulCount: Int,
    val createdAt: Long,
    val isVerifiedPurchase: Boolean,
    val authorName: String = ""
)

data class User(
    val id: Long,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String,
    val bio: String,
    val location: String,
    val reviewCount: Int,
    val followersCount: Int,
    val followingCount: Int,
    val isCurrentUser: Boolean,
    val createdAt: Long
)

data class Brand(
    val id: Long,
    val name: String,
    val country: String,
    val logoUrl: String,
    val description: String,
    val sustainabilityScore: Int,
    val priceRange: String,
    val itemCount: Int,
    val averageRating: Float
)

// --- Mappers ---

fun ClothingItemEntity.toDomain() = ClothingItem(
    id = id, name = name, brand = brand, category = category,
    material = material, description = description, imageUrl = imageUrl,
    priceRange = priceRange, averageRating = averageRating,
    reviewCount = reviewCount, weatherSuitability = weatherSuitability,
    createdAt = createdAt, isWishlisted = isWishlisted
)

fun ClothingItem.toEntity() = ClothingItemEntity(
    id = id, name = name, brand = brand, category = category,
    material = material, description = description, imageUrl = imageUrl,
    priceRange = priceRange, averageRating = averageRating,
    reviewCount = reviewCount, weatherSuitability = weatherSuitability,
    createdAt = createdAt, isWishlisted = isWishlisted
)

fun ReviewEntity.toDomain(authorName: String = "") = Review(
    id = id, clothingItemId = clothingItemId, userId = userId,
    rating = rating, title = title, body = body,
    pros = if (pros.isBlank()) emptyList() else pros.split(",").map { it.trim() },
    cons = if (cons.isBlank()) emptyList() else cons.split(",").map { it.trim() },
    weatherCondition = weatherCondition,
    fitRating = fitRating, warmthRating = warmthRating,
    durabilityRating = durabilityRating, valueRating = valueRating,
    helpfulCount = helpfulCount, createdAt = createdAt,
    isVerifiedPurchase = isVerifiedPurchase, authorName = authorName
)

fun Review.toEntity() = ReviewEntity(
    id = id, clothingItemId = clothingItemId, userId = userId,
    rating = rating, title = title, body = body,
    pros = pros.joinToString(","),
    cons = cons.joinToString(","),
    weatherCondition = weatherCondition,
    fitRating = fitRating, warmthRating = warmthRating,
    durabilityRating = durabilityRating, valueRating = valueRating,
    helpfulCount = helpfulCount, createdAt = createdAt,
    isVerifiedPurchase = isVerifiedPurchase
)

fun UserEntity.toDomain() = User(
    id = id, username = username, displayName = displayName,
    email = email, avatarUrl = avatarUrl, bio = bio, location = location,
    reviewCount = reviewCount, followersCount = followersCount,
    followingCount = followingCount, isCurrentUser = isCurrentUser,
    createdAt = createdAt
)

fun BrandEntity.toDomain() = Brand(
    id = id, name = name, country = country, logoUrl = logoUrl,
    description = description, sustainabilityScore = sustainabilityScore,
    priceRange = priceRange, itemCount = itemCount, averageRating = averageRating
)

fun User.toEntity() = UserEntity(
    id = id, username = username, displayName = displayName,
    email = email, avatarUrl = avatarUrl, bio = bio, location = location,
    reviewCount = reviewCount, followersCount = followersCount,
    followingCount = followingCount, isCurrentUser = isCurrentUser,
    createdAt = createdAt
)

fun Brand.toEntity() = BrandEntity(
    id = id, name = name, country = country, logoUrl = logoUrl,
    description = description, sustainabilityScore = sustainabilityScore,
    priceRange = priceRange, itemCount = itemCount, averageRating = averageRating
)
