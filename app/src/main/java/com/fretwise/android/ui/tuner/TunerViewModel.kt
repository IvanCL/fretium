package com.fretwise.android.ui.tuner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fretwise.android.audio.NoteUtils
import com.fretwise.android.audio.TunerEngine
import com.fretwise.android.data.model.GuitarString
import com.fretwise.android.data.model.TunerReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TunerUiState(
    val isListening: Boolean = false,
    val frequency: Double? = null,
    val noteName: String = "—",
    val cents: Int = 0,
    val closestString: GuitarString? = null,
    val statusMessage: String = "",
    val errorMessage: String? = null,
)

@HiltViewModel
class TunerViewModel @Inject constructor(
    private val tunerEngine: TunerEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TunerUiState())
    val uiState: StateFlow<TunerUiState> = _uiState.asStateFlow()

    private var listenJob: Job? = null

    fun startListening() {
        if (listenJob?.isActive == true) return
        _uiState.update {
            TunerUiState(isListening = true, statusMessage = "🟢 Micrófono activo — toca una cuerda")
        }
        listenJob = viewModelScope.launch {
            tunerEngine.detectPitch()
                .catch {
                    _uiState.update {
                        it.copy(
                            isListening = false,
                            errorMessage = "❌ No se pudo acceder al micrófono. Revisa los permisos.",
                        )
                    }
                }
                .collect { freq -> onFrequencyDetected(freq) }
        }
    }

    fun stopListening() {
        listenJob?.cancel()
        listenJob = null
        _uiState.value = TunerUiState()
    }

    private fun onFrequencyDetected(freq: Double) {
        if (freq <= 0) {
            _uiState.update { it.copy(frequency = null, noteName = "—", cents = 0, closestString = null) }
            return
        }
        val note = NoteUtils.freqToNote(freq)
        val closest = TunerReference.closestString(freq)
        _uiState.update {
            it.copy(
                isListening = true,
                frequency = freq,
                noteName = note.name,
                cents = note.cents,
                closestString = closest,
            )
        }
    }

    override fun onCleared() {
        listenJob?.cancel()
    }
}
