package com.fretwise.android.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.data.model.Song

@Composable
fun SongsScreen(onSongClick: (String) -> Unit, viewModel: SongsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("Canciones", style = MaterialTheme.typography.titleLarge) }

        items(uiState.songs, key = { it.id }) { song ->
            SongCard(song = song, onClick = { onSongClick(song.id) })
        }
    }
}

@Composable
private fun SongCard(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        onClick = onClick,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(song.title, style = MaterialTheme.typography.titleLarge)
            Text(song.description, style = MaterialTheme.typography.bodyLarge)
            Text(
                "${song.defaultBpm} BPM · ${song.timeSignature}/4 · ${song.origin}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
