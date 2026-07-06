package com.fretwise.android.data.model

data class SongChord(val chordId: String, val beats: Int)

data class Song(
    val id: String,
    val title: String,
    val description: String,
    val origin: String,
    val level: Level,
    val defaultBpm: Int,
    val timeSignature: Int,
    val sequence: List<SongChord>,
    val strumHint: String,
    val tips: List<String>,
)
