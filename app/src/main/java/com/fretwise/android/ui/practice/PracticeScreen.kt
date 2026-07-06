package com.fretwise.android.ui.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.ui.chords.components.ChordDiagram

@Composable
fun PracticeScreen(viewModel: PracticeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.phase) {
        PracticePhase.INTRO -> IntroContent(onStart = viewModel::start)
        PracticePhase.QUIZ -> QuizContent(
            uiState = uiState,
            onOk = viewModel::markOk,
            onHard = viewModel::markHard,
            onSkip = viewModel::skip,
            onListen = viewModel::listenCurrent,
        )
        PracticePhase.FINISHED -> FinishedContent(uiState = uiState, onRestart = viewModel::restart)
    }
}

@Composable
private fun IntroContent(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Modo práctica", style = MaterialTheme.typography.titleLarge)
        Text(
            "Te mostraremos acordes al azar de tu nivel. Marca si lo tocaste bien o si te costó.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStart) { Text("Empezar") }
    }
}

@Composable
private fun QuizContent(
    uiState: PracticeUiState,
    onOk: () -> Unit,
    onHard: () -> Unit,
    onSkip: () -> Unit,
    onListen: () -> Unit,
) {
    val chord = uiState.current ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${uiState.stats.total} practicados")
            Text("✓ ${uiState.stats.ok}   😓 ${uiState.stats.hard}")
        }

        Spacer(Modifier.height(24.dp))
        Text(chord.name, style = MaterialTheme.typography.titleLarge)
        Text(chord.fullName, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))
        ChordDiagram(chord = chord, modifier = Modifier.width(160.dp))

        uiState.currentTip?.let { tip ->
            Spacer(Modifier.height(16.dp))
            Text("💡 $tip", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onListen) {
            Icon(Icons.Filled.VolumeUp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Escuchar acorde")
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onOk,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text("Lo toqué bien")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onHard, modifier = Modifier.fillMaxWidth()) {
            Text("Me cuesta")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text("Saltar")
        }
    }
}

@Composable
private fun FinishedContent(uiState: PracticeUiState, onRestart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("¡Sesión completa!", style = MaterialTheme.typography.titleLarge)
            Text(
                "${uiState.stats.total} acordes practicados · ${uiState.stats.ok} bien · ${uiState.stats.hard} para mejorar",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRestart) { Text("Practicar de nuevo") }
        }

        Spacer(Modifier.height(24.dp))
        Text("En esta sesión", style = MaterialTheme.typography.bodyLarge)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(uiState.sessionLog) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${entry.chord.name} — ${entry.chord.fullName}")
                    Text(
                        when (entry.result) {
                            LogResult.OK -> "✓"
                            LogResult.HARD -> "😓"
                            LogResult.SKIP -> "⏭"
                        },
                    )
                }
            }
        }
    }
}
