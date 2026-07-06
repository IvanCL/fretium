package com.fretwise.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fretwise.android.data.local.dao.ProgressDao
import com.fretwise.android.data.local.dao.UserDao
import com.fretwise.android.data.local.entity.ProgressEntity
import com.fretwise.android.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ProgressEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun progressDao(): ProgressDao

    companion object {
        const val DATABASE_NAME = "fretium.db"
    }
}
