package com.outwear.app.model.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val logoUrl: String = "",
    val description: String = "",
    val websiteUrl: String = "",
    val sustainabilityScore: Int = 0, // 0-100
    val priceRange: String = "Mid-range",
    val itemCount: Int = 0,
    val averageRating: Float = 0f
)
