package com.fretwise.android.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.data.SongRepository
import com.fretwise.android.data.UserRepository
import com.fretwise.android.data.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongsUiState(val isLoading: Boolean = true, val songs: List<Song> = emptyList())

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _uiState.value = if (user == null) {
                    SongsUiState(isLoading = false)
                } else {
                    SongsUiState(isLoading = false, songs = songRepository.songsUpToLevel(user.level))
                }
            }
        }
    }
}
