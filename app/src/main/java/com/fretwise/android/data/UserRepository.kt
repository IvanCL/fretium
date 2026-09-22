package com.fretwise.android.data

import com.fretwise.android.data.local.SessionPreferences
import com.fretwise.android.data.local.dao.UserDao
import com.fretwise.android.data.local.entity.UserEntity
import com.fretwise.android.data.model.AppUser
import com.fretwise.android.data.model.Level
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ProfileResult {
    data class Success(val user: AppUser) : ProfileResult
    data class Error(val message: String) : ProfileResult
}

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionPreferences: SessionPreferences,
) {

    /** Emits the currently active local profile, or null when none is selected. Backed by DataStore + Room. */
    val currentUser: Flow<AppUser?> = sessionPreferences.activeUserId
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else userDao.observeById(userId).map { it?.toAppUser() }
        }

    /**
     * Enters a local profile by name — no password, no server. If the name already exists
     * it resumes that profile's progress; otherwise it creates a new one. Everything stays
     * on-device, so there's nothing to authenticate against.
     */
    suspend fun enterProfile(name: String): ProfileResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return ProfileResult.Error("El nombre no puede estar vacío")

        val existing = userDao.findByName(trimmed)
        val user = existing ?: userDao.insert(UserEntity(name = trimmed)).let { id ->
            UserEntity(id = id, name = trimmed)
        }
        sessionPreferences.setActiveUser(user.id)
        return ProfileResult.Success(user.toAppUser())
    }

    suspend fun logout() {
        sessionPreferences.clearSession()
    }

    suspend fun updateLevel(userId: Long, level: Level) {
        userDao.updateLevel(userId, level.id)
    }

    private fun UserEntity.toAppUser() = AppUser(id = id, name = name, level = Level.fromId(level))
}
