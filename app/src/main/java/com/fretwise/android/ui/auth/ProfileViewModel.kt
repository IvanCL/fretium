package com.fretwise.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.data.ProfileResult
import com.fretwise.android.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

/** No password, no server — just picks (or creates) a local progress profile by name. */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

    fun submit() {
        val current = _uiState.value
        if (current.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = userRepository.enterProfile(current.name)) {
                is ProfileResult.Success -> _uiState.update { it.copy(isLoading = false) }
                is ProfileResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
