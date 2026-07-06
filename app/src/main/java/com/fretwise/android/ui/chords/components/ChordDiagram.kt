package com.fretwise.android.ui.chords.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.fretwise.android.data.model.Chord

// Proportions ported 1:1 from the web app's src/lib/chordSvg.ts (viewBox 110x140).
private const val DESIGN_W = 110f
private const val DESIGN_H = 140f
private val STRING_X = listOf(15f, 31f, 47f, 63f, 79f, 95f).map { it / DESIGN_W }
private val FRET_Y = listOf(30f, 52f, 74f, 96f, 118f).map { it / DESIGN_H }
private val DOT_Y = listOf(41f, 63f, 85f, 107f).map { it / DESIGN_H }
private const val SYM_Y = 18f / DESIGN_H
private const val DOT_RADIUS_RATIO = 8f / DESIGN_W

/**
 * Draws a fretboard chord diagram entirely with Canvas primitives — no images or SVG assets.
 * Renders strings, frets, an optional barre, finger dots with numbers, and mute/open symbols.
 */
@Composable
fun ChordDiagram(
    chord: Chord,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF475569),
    nutColor: Color = Color(0xFFF1F5F9),
    fgColor: Color = Color(0xFFF1F5F9),
    dotColor: Color = Color(0xFFF59E0B),
    dotTextColor: Color = Color(0xFF1E293B),
) {
    Canvas(modifier = modifier.aspectRatio(DESIGN_W / DESIGN_H)) {
        val w = size.width
        val h = size.height
        val stringX = STRING_X.map { it * w }
        val fretY = FRET_Y.map { it * h }
        val dotY = DOT_Y.map { it * h }
        val symY = SYM_Y * h
        val dotRadius = w * DOT_RADIUS_RATIO

        val fretLabelPaint = Paint().apply {
            color = fgColor.toArgb()
            textSize = h * 0.08f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.MONOSPACE
        }
        val fingerPaint = Paint().apply {
            color = dotTextColor.toArgb()
            textSize = h * 0.065f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        if (chord.startFret > 1) {
            drawContext.canvas.nativeCanvas.drawText(
                "${chord.startFret}fr",
                stringX[0] - dotRadius * 1.6f,
                fretY[0] + h * 0.1f,
                fretLabelPaint,
            )
        }

        // Horizontal fret lines (nut is thicker when the chord starts at fret 1).
        fretY.forEachIndexed { index, y ->
            val isNut = index == 0 && chord.startFret == 1
            drawLine(
                color = if (isNut) nutColor else lineColor,
                start = androidx.compose.ui.geometry.Offset(stringX.first(), y),
                end = androidx.compose.ui.geometry.Offset(stringX.last(), y),
                strokeWidth = if (isNut) h * 0.028f else h * 0.01f,
            )
        }

        // Vertical string lines.
        stringX.forEach { x ->
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(x, fretY.first()),
                end = androidx.compose.ui.geometry.Offset(x, fretY.last()),
                strokeWidth = h * 0.01f,
            )
        }

        // Barre bar, drawn before finger dots so per-string dots can render on top of it.
        chord.barre?.let { barre ->
            val fretIdx = barre.fret - chord.startFret
            if (fretIdx in 0..3) {
                val y = dotY[fretIdx]
                val x1 = stringX[6 - barre.fromString]
                val x2 = stringX[6 - barre.toString]
                drawRoundRect(
                    color = dotColor,
                    topLeft = androidx.compose.ui.geometry.Offset(x1, y - dotRadius),
                    size = androidx.compose.ui.geometry.Size(x2 - x1, dotRadius * 2),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(dotRadius, dotRadius),
                )
            }
        }

        // Per-string finger dots and open/muted symbols.
        chord.positions.forEachIndexed { s, pos ->
            val x = stringX[s]
            when {
                pos == -1 -> {
                    val r = w * 0.045f
                    drawLine(fgColor, androidx.compose.ui.geometry.Offset(x - r, symY - r), androidx.compose.ui.geometry.Offset(x + r, symY + r), strokeWidth = h * 0.014f)
                    drawLine(fgColor, androidx.compose.ui.geometry.Offset(x + r, symY - r), androidx.compose.ui.geometry.Offset(x - r, symY + r), strokeWidth = h * 0.014f)
                }
                pos == 0 -> {
                    drawCircle(
                        color = fgColor,
                        radius = w * 0.045f,
                        center = androidx.compose.ui.geometry.Offset(x, symY),
                        style = Stroke(width = h * 0.014f),
                    )
                }
                else -> {
                    val fretIdx = pos - chord.startFret
                    if (fretIdx in 0..3) {
                        val y = dotY[fretIdx]
                        drawCircle(color = dotColor, radius = dotRadius, center = androidx.compose.ui.geometry.Offset(x, y))
                        chord.fingers.getOrNull(s)?.let { finger ->
                            drawContext.canvas.nativeCanvas.drawText(
                                finger.toString(),
                                x,
                                y + h * 0.028f,
                                fingerPaint,
                            )
                        }
                    }
                }
            }
        }
    }
}
