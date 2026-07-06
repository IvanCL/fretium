package com.fretwise.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "fretium_session")

/** Persists the active logged-in user id so the app doesn't require logging in again. */
@Singleton
class SessionPreferences @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
) {
    private val userIdKey = longPreferencesKey("active_user_id")

    val activeUserId: Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[userIdKey]?.takeIf { it > 0 }
    }

    suspend fun setActiveUser(userId: Long) {
        context.sessionDataStore.edit { it[userIdKey] = userId }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { it.remove(userIdKey) }
    }
}
