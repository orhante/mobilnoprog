package com.outwear.app.model.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String = "",
    val bio: String = "",
    val location: String = "",
    val reviewCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isCurrentUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
