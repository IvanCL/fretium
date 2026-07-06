package com.fretwise.android.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.data.model.Level

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToChords: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToSongs: () -> Unit,
    onNavigateToTuner: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val user = uiState.user ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Hola, ${user.name}", style = MaterialTheme.typography.titleLarge)
                    Text("Sigue practicando cada día", style = MaterialTheme.typography.bodyLarge)
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Progreso en tu nivel", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    val progressFraction = if (uiState.totalChordsForLevel > 0) {
                        uiState.chordsLearnedCount.toFloat() / uiState.totalChordsForLevel
                    } else 0f
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("${uiState.chordsLearnedCount} / ${uiState.totalChordsForLevel} acordes aprendidos")
                    Text("${uiState.totalPracticeCount} repeticiones practicadas en total")
                }
            }
        }

        item {
            Column {
                Text("Nivel actual", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(Level.entries) { level ->
                        LevelChip(
                            level = level,
                            selected = level == user.level,
                            onClick = { viewModel.selectLevel(level) },
                        )
                    }
                }
            }
        }

        item {
            Column {
                Text("Accesos rápidos", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                QuickAccessGrid(
                    onNavigateToChords = onNavigateToChords,
                    onNavigateToPractice = onNavigateToPractice,
                    onNavigateToSongs = onNavigateToSongs,
                    onNavigateToTuner = onNavigateToTuner,
                )
            }
        }
    }
}

@Composable
private fun LevelChip(level: Level, selected: Boolean, onClick: () -> Unit) {
    val color = Color(level.color)
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = if (selected) color.copy(alpha = 0.18f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(level.label, color = color, style = MaterialTheme.typography.bodyLarge)
            Text(level.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun QuickAccessGrid(
    onNavigateToChords: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToSongs: () -> Unit,
    onNavigateToTuner: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            QuickAccessCard("Acordes", Icons.Filled.LibraryMusic, Modifier.weight(1f), onNavigateToChords)
            QuickAccessCard("Práctica", Icons.Filled.MusicNote, Modifier.weight(1f), onNavigateToPractice)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            QuickAccessCard("Canciones", Icons.Filled.QueueMusic, Modifier.weight(1f), onNavigateToSongs)
            QuickAccessCard("Afinador", Icons.Filled.Tune, Modifier.weight(1f), onNavigateToTuner)
        }
    }
}

@Composable
private fun QuickAccessCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.height(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(label)
        }
    }
}
