package com.outwear.app.model.di

import com.outwear.app.model.repository.remote.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductNetworkRepository(
        impl: ProductNetworkRepositoryImpl
    ): ProductNetworkRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreWishlistRepository(
        impl: FirestoreWishlistRepositoryImpl
    ): FirestoreWishlistRepository
}
