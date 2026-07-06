package com.fretwise.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.data.ChordRepository
import com.fretwise.android.data.ProgressRepository
import com.fretwise.android.data.UserRepository
import com.fretwise.android.data.model.AppUser
import com.fretwise.android.data.model.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val user: AppUser? = null,
    val isLoading: Boolean = true,
    val chordsLearnedCount: Int = 0,
    val totalChordsForLevel: Int = 0,
    val totalPracticeCount: Int = 0,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository,
    private val chordRepository: ChordRepository,
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = userRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(DashboardUiState(user = null, isLoading = false))
            } else {
                progressRepository.observeProgress(user.id).map { progress ->
                    val chordsForLevel = chordRepository.chordsUpToLevel(user.level)
                    val learnedIds = progress.filter { it.learned }.map { it.chordName }.toSet()
                    DashboardUiState(
                        user = user,
                        isLoading = false,
                        chordsLearnedCount = chordsForLevel.count { it.id in learnedIds },
                        totalChordsForLevel = chordsForLevel.size,
                        totalPracticeCount = progress.sumOf { it.practiceCount },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun selectLevel(level: Level) {
        val userId = uiState.value.user?.id ?: return
        viewModelScope.launch { userRepository.updateLevel(userId, level) }
    }
}
