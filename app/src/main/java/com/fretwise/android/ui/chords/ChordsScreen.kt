package com.fretwise.android.ui.chords

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.ui.chords.components.ChordDiagram

@Composable
fun ChordsScreen(viewModel: ChordsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val learnedCount = uiState.items.count { it.learned }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Acordes", style = MaterialTheme.typography.titleLarge)
            Text("$learnedCount / ${uiState.items.size} aprendidos", style = MaterialTheme.typography.bodyLarge)
        }

        items(uiState.items, key = { it.chord.id }) { item ->
            ChordCard(
                item = item,
                onToggleLearned = { viewModel.toggleLearned(item.chord.id, item.learned) },
            )
        }
    }
}

@Composable
private fun ChordCard(item: ChordListItem, onToggleLearned: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            ChordDiagram(chord = item.chord, modifier = Modifier.width(90.dp))

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(item.chord.name, style = MaterialTheme.typography.titleLarge)
                        Text(item.chord.fullName, style = MaterialTheme.typography.bodyLarge)
                    }
                    Icon(
                        imageVector = if (item.learned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = if (item.learned) "Aprendido" else "Pendiente",
                    )
                }

                Spacer(Modifier.height(8.dp))
                item.chord.tips.forEach { tip ->
                    Text("• $tip", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onToggleLearned) {
                    Text(if (item.learned) "Marcar pendiente" else "Marcar aprendido")
                }
            }
        }
    }
}
