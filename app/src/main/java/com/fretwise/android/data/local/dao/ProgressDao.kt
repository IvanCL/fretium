package com.fretwise.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fretwise.android.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM progress WHERE user_id = :userId")
    fun observeForUser(userId: Long): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE user_id = :userId AND chord_name = :chordName LIMIT 1")
    suspend fun findOne(userId: Long, chordName: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: ProgressEntity)

    @Update
    suspend fun update(entity: ProgressEntity)

    /**
     * Mirrors the web app's `INSERT ... ON CONFLICT DO UPDATE` in progress.ts:
     * [learned] overrides the stored value only when non-null; [practiceIncrement]
     * is always added on top of the existing practice count.
     */
    @Transaction
    suspend fun upsert(userId: Long, chordName: String, learned: Boolean?, practiceIncrement: Int) {
        val existing = findOne(userId, chordName)
        if (existing == null) {
            insertOrReplace(
                ProgressEntity(
                    userId = userId,
                    chordName = chordName,
                    learned = learned ?: false,
                    practiceCount = practiceIncrement,
                ),
            )
        } else {
            update(
                existing.copy(
                    learned = learned ?: existing.learned,
                    practiceCount = existing.practiceCount + practiceIncrement,
                ),
            )
        }
    }
}
