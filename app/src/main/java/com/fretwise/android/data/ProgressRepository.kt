package com.fretwise.android.data

import com.fretwise.android.data.local.dao.ProgressDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class ChordProgress(
    val chordName: String,
    val learned: Boolean,
    val practiceCount: Int,
)

@Singleton
class ProgressRepository @Inject constructor(
    private val progressDao: ProgressDao,
) {
    fun observeProgress(userId: Long): Flow<List<ChordProgress>> =
        progressDao.observeForUser(userId).map { list ->
            list.map { ChordProgress(it.chordName, it.learned, it.practiceCount) }
        }

    suspend fun recordPractice(userId: Long, chordName: String, increment: Int = 1) {
        progressDao.upsert(userId, chordName, learned = null, practiceIncrement = increment)
    }

    suspend fun setLearned(userId: Long, chordName: String, learned: Boolean) {
        progressDao.upsert(userId, chordName, learned = learned, practiceIncrement = 0)
    }
}
