package com.fretwise.android.ui.songs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.audio.ChordAudioEngine
import com.fretwise.android.data.SongRepository
import com.fretwise.android.data.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToLong

data class SongPlayerUiState(
    val song: Song? = null,
    val bpm: Int = 90,
    val isPlaying: Boolean = false,
    val loop: Boolean = false,
    val currentIdx: Int = 0,
    val currentBeat: Int = 0,
    val rounds: Int = 0,
    val elapsedMs: Long = 0,
    val isFinished: Boolean = false,
)

@HiltViewModel
class SongPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val songRepository: SongRepository,
    private val chordAudioEngine: ChordAudioEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongPlayerUiState())
    val uiState: StateFlow<SongPlayerUiState> = _uiState.asStateFlow()

    private var tickJob: Job? = null
    private var timerJob: Job? = null
    private var elapsedBaseMs = 0L

    init {
        val songId: String = savedStateHandle.get<String>("songId").orEmpty()
        val song = songRepository.byId(songId)
        _uiState.value = SongPlayerUiState(song = song, bpm = song?.defaultBpm ?: 90)
    }

    fun setBpm(bpm: Int) = _uiState.update { it.copy(bpm = bpm) }

    fun toggleLoop() = _uiState.update { it.copy(loop = !it.loop) }

    fun play() {
        if (_uiState.value.isPlaying || _uiState.value.song == null) return
        _uiState.update { it.copy(isPlaying = true, isFinished = false) }
        tick()
        tickJob = viewModelScope.launch {
            while (isActive) {
                delay(intervalMs())
                tick()
            }
        }
        timerJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis() - elapsedBaseMs
            while (isActive) {
                delay(500)
                _uiState.update { it.copy(elapsedMs = System.currentTimeMillis() - startedAt) }
            }
        }
    }

    fun pause() {
        tickJob?.cancel()
        timerJob?.cancel()
        elapsedBaseMs = _uiState.value.elapsedMs
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun stop() {
        tickJob?.cancel()
        timerJob?.cancel()
        elapsedBaseMs = 0
        _uiState.update {
            it.copy(
                isPlaying = false,
                isFinished = false,
                currentIdx = 0,
                currentBeat = 0,
                rounds = 0,
                elapsedMs = 0,
            )
        }
    }

    fun restart() {
        stop()
        play()
    }

    private fun intervalMs(): Long = (60_000.0 / _uiState.value.bpm.coerceAtLeast(1)).roundToLong()

    private fun tick() {
        val state = _uiState.value
        val song = state.song ?: return
        val entry = song.sequence[state.currentIdx]
        chordAudioEngine.playChord(entry.chordId)

        var nextBeat = state.currentBeat + 1
        var nextIdx = state.currentIdx
        var nextRounds = state.rounds

        if (nextBeat >= entry.beats) {
            nextBeat = 0
            nextIdx++
            if (nextIdx >= song.sequence.size) {
                nextRounds++
                if (!state.loop) {
                    finishPlayback(nextRounds)
                    return
                }
                nextIdx = 0
            }
        }

        _uiState.update { it.copy(currentIdx = nextIdx, currentBeat = nextBeat, rounds = nextRounds) }
    }

    private fun finishPlayback(rounds: Int) {
        tickJob?.cancel()
        timerJob?.cancel()
        _uiState.update { it.copy(isPlaying = false, isFinished = true, rounds = rounds) }
    }
}
