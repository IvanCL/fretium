package com.fretwise.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["name"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val level: String = "beginner",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() / 1000,
)
