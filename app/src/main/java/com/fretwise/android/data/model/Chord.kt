package com.fretwise.android.data.model

/**
 * @param positions per string, index 0 = string 6 (low E) ... index 5 = string 1 (high e).
 *   -1 = muted, 0 = open, N = fret number.
 * @param fingers finger number per string (null = no finger / muted / open).
 */
data class Barre(val fret: Int, val fromString: Int, val toString: Int)

data class Chord(
    val id: String,
    val name: String,
    val fullName: String,
    val level: Level,
    val positions: List<Int>,
    val fingers: List<Int?>,
    val startFret: Int,
    val barre: Barre? = null,
    val tips: List<String>,
)
