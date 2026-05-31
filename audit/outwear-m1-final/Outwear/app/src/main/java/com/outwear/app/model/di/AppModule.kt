package com.outwear.app.model.di

import android.content.Context
import androidx.room.Room
import com.outwear.app.model.data.local.dao.*
import com.outwear.app.model.data.local.db.OutwearDatabase
import com.outwear.app.model.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OutwearDatabase =
        Room.databaseBuilder(context, OutwearDatabase::class.java, "outwear_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideClothingItemDao(db: OutwearDatabase): ClothingItemDao = db.clothingItemDao()

    @Provides
    fun provideReviewDao(db: OutwearDatabase): ReviewDao = db.reviewDao()

    @Provides
    fun provideUserDao(db: OutwearDatabase): UserDao = db.userDao()

    @Provides
    fun provideBrandDao(db: OutwearDatabase): BrandDao = db.brandDao()

    @Provides
    fun provideWishlistDao(db: OutwearDatabase): WishlistDao = db.wishlistDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClothingRepository(impl: ClothingRepositoryImpl): ClothingRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindBrandRepository(impl: BrandRepositoryImpl): BrandRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository
}
