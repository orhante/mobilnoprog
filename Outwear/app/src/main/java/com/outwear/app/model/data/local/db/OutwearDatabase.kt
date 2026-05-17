package com.outwear.app.model.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.outwear.app.model.data.local.dao.*
import com.outwear.app.model.data.local.entity.*

@Database(
    entities = [
        ClothingItemEntity::class,
        ReviewEntity::class,
        UserEntity::class,
        BrandEntity::class,
        WishlistItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OutwearDatabase : RoomDatabase() {
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun brandDao(): BrandDao
    abstract fun wishlistDao(): WishlistDao
}
