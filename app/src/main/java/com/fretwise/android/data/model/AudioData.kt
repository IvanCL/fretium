package com.fretwise.android.data.model

/**
 * Per-note frequencies (Hz) for each chord when strummed open in standard tuning.
 * Migrated 1:1 from CHORD_FREQS in songs.astro / practice.astro.
 */
object ChordFrequencies {
    val MAP: Map<String, List<Double>> = mapOf(
        "Em" to listOf(82.41, 123.47, 164.81, 196.00, 246.94, 329.63),
        "Am" to listOf(110.00, 164.81, 220.00, 261.63, 329.63),
        "E" to listOf(82.41, 123.47, 164.81, 207.65, 246.94, 329.63),
        "A" to listOf(110.00, 164.81, 220.00, 277.18, 329.63),
        "D" to listOf(146.83, 220.00, 293.66, 369.99),
        "G" to listOf(98.00, 123.47, 146.83, 196.00, 246.94, 392.00),
        "C" to listOf(130.81, 164.81, 196.00, 261.63, 329.63),
        "Bm" to listOf(123.47, 185.00, 246.94, 293.66, 369.99),
        "F" to listOf(87.31, 130.81, 174.61, 220.00, 261.63, 349.23),
        "Dm" to listOf(146.83, 220.00, 293.66, 349.23),
        "A7" to listOf(110.00, 164.81, 196.00, 277.18, 329.63),
        "D7" to listOf(146.83, 220.00, 261.63, 369.99),
        "G7" to listOf(98.00, 123.47, 146.83, 174.61, 246.94, 392.00),
        "C7" to listOf(130.81, 164.81, 196.00, 233.08, 329.63),
        "Em7" to listOf(82.41, 123.47, 146.83, 196.00, 246.94, 329.63),
        "Am7" to listOf(110.00, 164.81, 196.00, 261.63, 329.63),
        "Dm7" to listOf(146.83, 220.00, 261.63, 349.23),
        "Cmaj7" to listOf(130.81, 164.81, 196.00, 246.94, 329.63),
        "Fmaj7" to listOf(87.31, 130.81, 174.61, 220.00, 261.63, 329.63),
        "Bm7" to listOf(123.47, 185.00, 220.00, 293.66, 369.99),
    )

    fun forChord(chordId: String): List<Double>? = MAP[chordId]
}

/** Standard EADGBE tuning reference for the chromatic tuner. */
data class GuitarString(val note: String, val frequency: Double)

object TunerReference {
    val STRINGS: List<GuitarString> = listOf(
        GuitarString("E2", 82.41),
        GuitarString("A2", 110.00),
        GuitarString("D3", 146.83),
        GuitarString("G3", 196.00),
        GuitarString("B3", 246.94),
        GuitarString("E4", 329.63),
    )

    fun closestString(freq: Double): GuitarString =
        STRINGS.minBy { kotlin.math.abs(it.frequency - freq) }
}
