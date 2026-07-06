package com.fretwise.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.data.UserRepository
import com.fretwise.android.data.model.AppUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val user: AppUser) : SessionState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _state.value = if (user != null) SessionState.LoggedIn(user) else SessionState.LoggedOut
            }
        }
    }

    fun logout() {
        viewModelScope.launch { userRepository.logout() }
    }
}
