package com.outwear.app.model.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,        // Jacket, Coat, Vest, Hoodie, etc.
    val material: String,
    val description: String,
    val imageUrl: String,
    val priceRange: String,      // Budget / Mid-range / Premium / Luxury
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val weatherSuitability: String, // Rain / Snow / Wind / All-season
    val createdAt: Long = System.currentTimeMillis(),
    val isWishlisted: Boolean = false
)
