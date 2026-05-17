package com.outwear.app.model.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wishlist_items",
    primaryKeys = ["userId", "clothingItemId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["clothingItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("clothingItemId")]
)
data class WishlistItemEntity(
    val userId: Long,
    val clothingItemId: Long,
    val addedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)
