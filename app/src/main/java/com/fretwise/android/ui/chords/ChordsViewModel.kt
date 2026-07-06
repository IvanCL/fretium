package com.fretwise.android.ui.chords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.data.ChordRepository
import com.fretwise.android.data.ProgressRepository
import com.fretwise.android.data.UserRepository
import com.fretwise.android.data.model.Chord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChordListItem(val chord: Chord, val learned: Boolean)

data class ChordsUiState(
    val isLoading: Boolean = true,
    val userId: Long? = null,
    val items: List<ChordListItem> = emptyList(),
)

@HiltViewModel
class ChordsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository,
    private val chordRepository: ChordRepository,
) : ViewModel() {

    val uiState: StateFlow<ChordsUiState> = userRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(ChordsUiState(isLoading = false))
            } else {
                val chords = chordRepository.chordsUpToLevel(user.level)
                progressRepository.observeProgress(user.id).map { progress ->
                    val learnedIds = progress.filter { it.learned }.map { it.chordName }.toSet()
                    ChordsUiState(
                        isLoading = false,
                        userId = user.id,
                        items = chords.map { ChordListItem(it, learned = it.id in learnedIds) },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChordsUiState())

    fun toggleLearned(chordId: String, currentlyLearned: Boolean) {
        val userId = uiState.value.userId ?: return
        viewModelScope.launch {
            progressRepository.setLearned(userId, chordId, !currentlyLearned)
        }
    }
}
