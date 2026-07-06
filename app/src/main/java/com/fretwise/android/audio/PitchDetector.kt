package com.fretwise.android.audio

import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

/**
 * Autocorrelation-based fundamental frequency detector, ported 1:1 from the
 * `autocorrelate()` function in the web app's tuner.astro.
 */
object PitchDetector {

    /** Returns the detected fundamental frequency in Hz, or -1 if no clear pitch is present. */
    fun autocorrelate(buffer: FloatArray, sampleRate: Int): Double {
        val size = buffer.size
        val rms = kotlin.math.sqrt(buffer.sumOf { (it * it).toDouble() } / size)
        if (rms < 0.01) return -1.0

        val threshold = 0.2f
        var r1 = 0
        var r2 = size - 1
        for (i in 0 until size / 2) {
            if (abs(buffer[i]) < threshold) { r1 = i; break }
        }
        for (i in 1 until size / 2) {
            if (abs(buffer[size - i]) < threshold) { r2 = size - i; break }
        }

        val trimmed = buffer.copyOfRange(r1, r2.coerceAtLeast(r1 + 1))
        val n = trimmed.size
        if (n < 2) return -1.0

        val c = DoubleArray(n)
        for (i in 0 until n) {
            var sum = 0.0
            for (j in 0 until n - i) {
                sum += trimmed[j] * trimmed[j + i]
            }
            c[i] = sum
        }

        var d = 0
        while (d < n - 1 && c[d] > c[d + 1]) d++

        var maxVal = -1.0
        var maxPos = -1
        for (i in d until n) {
            if (c[i] > maxVal) {
                maxVal = c[i]
                maxPos = i
            }
        }
        if (maxPos < 0) return -1.0

        val y1 = if (maxPos - 1 >= 0) c[maxPos - 1] else c[maxPos]
        val y2 = c[maxPos]
        val y3 = if (maxPos + 1 < n) c[maxPos + 1] else c[maxPos]
        val a = (y1 + y3 - 2 * y2) / 2
        val offset = if (a != 0.0) -(y3 - y1) / (4 * a) else 0.0

        return sampleRate / (maxPos + offset)
    }
}

data class DetectedNote(val name: String, val cents: Int)

object NoteUtils {
    private val NOTES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    /** Mirrors freqToNote() from tuner.astro: MIDI number from A4=440Hz reference. */
    fun freqToNote(freq: Double): DetectedNote {
        val midi = 12 * log2(freq / 440.0) + 69
        val rounded = midi.roundToInt()
        val cents = ((midi - rounded) * 100).roundToInt()
        val octave = Math.floorDiv(rounded, 12) - 1
        val note = NOTES[((rounded % 12) + 12) % 12]
        return DetectedNote("$note$octave", cents)
    }
}
