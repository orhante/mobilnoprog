package com.outwear.app.model.data.local.dao

import androidx.room.*
import com.outwear.app.model.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY username")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET reviewCount = reviewCount + 1 WHERE id = :id")
    suspend fun incrementReviewCount(id: Long)

    @Query("UPDATE users SET displayName = :name, bio = :bio, location = :location WHERE id = :id")
    suspend fun updateProfile(id: Long, name: String, bio: String, location: String)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
