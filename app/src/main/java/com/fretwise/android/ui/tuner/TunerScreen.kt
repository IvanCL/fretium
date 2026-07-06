package com.fretwise.android.ui.tuner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fretwise.android.data.model.TunerReference
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TunerScreen(viewModel: TunerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
        if (granted) viewModel.startListening()
    }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Text("Afinador cromático", style = MaterialTheme.typography.titleLarge) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(uiState.noteName, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    val subtitle = uiState.frequency?.let { freq ->
                        val closest = uiState.closestString?.note ?: "?"
                        "%.1f Hz · Cuerda más cercana: %s".format(freq, closest)
                    } ?: "Toca una cuerda para detectar la nota"
                    Text(subtitle, style = MaterialTheme.typography.bodySmall)

                    Spacer(Modifier.height(16.dp))
                    TunerNeedle(cents = uiState.cents, hasSignal = uiState.frequency != null)

                    Spacer(Modifier.height(8.dp))
                    val inTune = uiState.frequency != null && kotlin.math.abs(uiState.cents) < 5
                    Text(
                        when {
                            uiState.frequency == null -> "—"
                            inTune -> "✓ ¡Afinada!"
                            uiState.cents > 0 -> "+${uiState.cents}¢ (demasiado alta)"
                            else -> "${uiState.cents}¢ (demasiado baja)"
                        },
                        color = if (inTune) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    )

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (uiState.isListening) {
                            viewModel.stopListening()
                        } else if (hasPermission) {
                            viewModel.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }) {
                        Text(if (uiState.isListening) "Detener" else "Activar micrófono")
                    }

                    uiState.errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        item {
            Column {
                Text("Cuerdas en afinación estándar (EADGBE)", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(90.dp),
                ) {
                    items(TunerReference.STRINGS) { string ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(string.note, style = MaterialTheme.typography.bodyLarge)
                            Text("%.1f Hz".format(string.frequency), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text("Consejos para afinar", style = MaterialTheme.typography.bodyLarge)
                listOf(
                    "Toca la cuerda al aire (sin pisarla) cerca del micrófono",
                    "La aguja debe apuntar al centro (0¢) para afinación perfecta",
                    "Si la aguja está a la izquierda, la cuerda está baja (afloja)",
                    "Si la aguja está a la derecha, la cuerda está alta (aprieta)",
                    "Afina siempre de abajo hacia arriba: sube la tensión gradualmente",
                ).forEach { tip -> Text("• $tip", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun TunerNeedle(cents: Int, hasSignal: Boolean) {
    val clamped = cents.coerceIn(-50, 50)
    val targetDegrees = if (hasSignal) clamped * 1.5f else 0f
    val animatedDegrees by animateFloatAsState(targetValue = targetDegrees, label = "needle")

    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
        val pivot = Offset(size.width / 2f, size.height * 0.9f)
        val radius = size.height * 0.85f

        // Arc track
        drawArc(
            color = Color(0xFF2E2E40),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(pivot.x - radius, pivot.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = 8f),
        )

        // Needle
        val angleRad = ((animatedDegrees - 90f) * PI / 180f)
        val tip = Offset(
            x = pivot.x + radius * 0.75f * cos(angleRad).toFloat(),
            y = pivot.y + radius * 0.75f * sin(angleRad).toFloat(),
        )
        drawLine(color = Color(0xFF8B5CF6), start = pivot, end = tip, strokeWidth = 6f)
        drawCircle(color = Color(0xFF8B5CF6), radius = 8f, center = pivot)
    }
}
