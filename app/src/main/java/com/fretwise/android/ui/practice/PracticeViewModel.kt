package com.fretwise.android.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.audio.ChordAudioEngine
import com.fretwise.android.data.ChordRepository
import com.fretwise.android.data.ProgressRepository
import com.fretwise.android.data.UserRepository
import com.fretwise.android.data.model.Chord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PracticePhase { INTRO, QUIZ, FINISHED }
enum class LogResult { OK, HARD, SKIP }

data class PracticeStats(val total: Int = 0, val ok: Int = 0, val hard: Int = 0)
data class PracticeLogEntry(val chord: Chord, val result: LogResult)

data class PracticeUiState(
    val phase: PracticePhase = PracticePhase.INTRO,
    val current: Chord? = null,
    val currentTip: String? = null,
    val stats: PracticeStats = PracticeStats(),
    val sessionLog: List<PracticeLogEntry> = emptyList(),
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository,
    private val chordRepository: ChordRepository,
    private val chordAudioEngine: ChordAudioEngine,
) : ViewModel() {

    private var userId: Long? = null
    private var pool: List<Chord> = emptyList()
    private val queue = ArrayDeque<Chord>()

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                userId = user?.id
                if (user != null) pool = chordRepository.chordsUpToLevel(user.level)
            }
        }
    }

    fun start() {
        queue.clear()
        queue.addAll(pool.shuffled())
        _uiState.value = PracticeUiState(phase = PracticePhase.QUIZ)
        showNext()
    }

    fun restart() = start()

    fun markOk() = recordResult(ok = true)
    fun markHard() = recordResult(ok = false)

    fun skip() {
        val current = _uiState.value.current ?: return
        _uiState.update { it.copy(sessionLog = listOf(PracticeLogEntry(current, LogResult.SKIP)) + it.sessionLog) }
        showNext()
    }

    fun listenCurrent() {
        _uiState.value.current?.let { chordAudioEngine.playChord(it.id) }
    }

    private fun recordResult(ok: Boolean) {
        val current = _uiState.value.current ?: return
        val uid = userId
        if (uid != null) {
            viewModelScope.launch { progressRepository.recordPractice(uid, current.id, 1) }
        }
        _uiState.update {
            val stats = it.stats.copy(
                total = it.stats.total + 1,
                ok = it.stats.ok + if (ok) 1 else 0,
                hard = it.stats.hard + if (!ok) 1 else 0,
            )
            val log = listOf(PracticeLogEntry(current, if (ok) LogResult.OK else LogResult.HARD)) + it.sessionLog
            it.copy(stats = stats, sessionLog = log)
        }
        if (!ok) queue.addFirst(current)
        showNext()
    }

    private fun showNext() {
        if (queue.isEmpty()) {
            _uiState.update { it.copy(phase = PracticePhase.FINISHED, current = null) }
            return
        }
        val next = queue.removeLast()
        _uiState.update { it.copy(current = next, currentTip = next.tips.randomOrNull(), phase = PracticePhase.QUIZ) }
    }
}
