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

sealed interface AuthResult {
    data class Success(val user: AppUser) : AuthResult
    data class Error(val message: String) : AuthResult
}

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionPreferences: SessionPreferences,
) {

    /** Emits the currently logged-in user, or null when logged out. Backed by DataStore + Room. */
    val currentUser: Flow<AppUser?> = sessionPreferences.activeUserId
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else userDao.observeById(userId).map { it?.toAppUser() }
        }

    suspend fun register(name: String, password: String): AuthResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return AuthResult.Error("El nombre no puede estar vacío")
        if (password.length < 4) return AuthResult.Error("La contraseña debe tener al menos 4 caracteres")
        if (userDao.findByName(trimmed) != null) {
            return AuthResult.Error("Ese nombre de usuario ya existe")
        }

        val entity = UserEntity(name = trimmed, passwordHash = PasswordHasher.hash(password))
        val id = userDao.insert(entity)
        sessionPreferences.setActiveUser(id)
        return AuthResult.Success(entity.copy(id = id).toAppUser())
    }

    suspend fun login(name: String, password: String): AuthResult {
        val user = userDao.findByName(name.trim())
            ?: return AuthResult.Error("Usuario o contraseña incorrectos")
        if (!PasswordHasher.verify(password, user.passwordHash)) {
            return AuthResult.Error("Usuario o contraseña incorrectos")
        }
        sessionPreferences.setActiveUser(user.id)
        return AuthResult.Success(user.toAppUser())
    }

    suspend fun logout() {
        sessionPreferences.clearSession()
    }

    suspend fun updateLevel(userId: Long, level: Level) {
        userDao.updateLevel(userId, level.id)
    }

    private fun UserEntity.toAppUser() = AppUser(id = id, name = name, level = Level.fromId(level))
}
