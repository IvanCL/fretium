package com.fretwise.android.ui.songs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.data.model.Song
import com.fretwise.android.ui.chords.components.ChordDiagram

@Composable
fun SongPlayerScreen(onBack: () -> Unit, viewModel: SongPlayerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val song = uiState.song

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") }
            Text(song?.title ?: "", style = MaterialTheme.typography.titleLarge)
        }

        if (song == null) return@Column

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    val entry = song.sequence[uiState.currentIdx]
                    val nextEntry = song.sequence[(uiState.currentIdx + 1) % song.sequence.size]

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(entry.chordId, style = MaterialTheme.typography.titleLarge)
                        ChordDiagram(chord = requireNotNull(com.fretwise.android.data.model.ChordsData.byId(entry.chordId)), modifier = Modifier.width(140.dp))
                        BeatDots(total = entry.beats, current = uiState.currentBeat)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
                        Text("Siguiente", style = MaterialTheme.typography.bodySmall)
                        Text(nextEntry.chordId)
                    }
                }
            }

            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("BPM: ${uiState.bpm}")
                        Text(song.strumHint)
                    }
                    Slider(
                        value = uiState.bpm.toFloat(),
                        onValueChange = { viewModel.setBpm(it.toInt()) },
                        valueRange = 40f..160f,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { if (uiState.isPlaying) viewModel.pause() else viewModel.play() }) {
                        Icon(if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = "Play/Pausa")
                    }
                    IconButton(onClick = viewModel::stop) {
                        Icon(Icons.Filled.Stop, contentDescription = "Detener")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.loop, onCheckedChange = { viewModel.toggleLoop() })
                        Text("Repetir")
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vueltas: ${uiState.rounds}")
                    Text("${uiState.currentIdx + 1} / ${song.sequence.size}")
                    Text(formatElapsed(uiState.elapsedMs))
                }
            }

            if (uiState.isFinished) {
                item { Text("¡Ronda completada! 🎉", style = MaterialTheme.typography.titleLarge) }
            }

            item { Text("Secuencia", style = MaterialTheme.typography.bodyLarge) }
            itemsIndexed(song.sequence) { index, entry ->
                val active = index == uiState.currentIdx
                Text(
                    "${entry.chordId} (${entry.beats} tiempos)",
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }

            item {
                Column {
                    Text("Consejos", style = MaterialTheme.typography.bodyLarge)
                    song.tips.forEach { tip -> Text("• $tip", style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

@Composable
private fun BeatDots(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val color = when {
                i < current -> MaterialTheme.colorScheme.primary
                i == current -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.outline
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}

private fun formatElapsed(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
