package com.outwear.app.model.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clothingItemId"), Index("userId")]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clothingItemId: Long,
    val userId: Long,
    val rating: Float,          // 1.0 - 5.0
    val title: String,
    val body: String,
    val pros: String,           // Comma-separated pros
    val cons: String,           // Comma-separated cons
    val weatherCondition: String,
    val fitRating: Int,         // 1-5
    val warmthRating: Int,      // 1-5
    val durabilityRating: Int,  // 1-5
    val valueRating: Int,       // 1-5
    val helpfulCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isVerifiedPurchase: Boolean = false
)
