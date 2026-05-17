package com.outwear.app.model.data.local.dao

import androidx.room.*
import com.outwear.app.model.data.local.entity.BrandEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandDao {

    @Query("SELECT * FROM brands ORDER BY name")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands WHERE id = :id")
    fun getBrandById(id: Long): Flow<BrandEntity?>

    @Query("SELECT * FROM brands WHERE name = :name LIMIT 1")
    fun getBrandByName(name: String): Flow<BrandEntity?>

    @Query("SELECT * FROM brands ORDER BY averageRating DESC LIMIT :limit")
    fun getTopBrands(limit: Int = 5): Flow<List<BrandEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrands(brands: List<BrandEntity>)

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Delete
    suspend fun deleteBrand(brand: BrandEntity)

    @Query("SELECT COUNT(*) FROM brands")
    suspend fun getBrandCount(): Int
}
