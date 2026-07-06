package com.fretwise.android.data

import com.fretwise.android.data.model.Chord
import com.fretwise.android.data.model.ChordsData
import com.fretwise.android.data.model.Level
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChordRepository @Inject constructor() {
    fun all(): List<Chord> = ChordsData.ALL
    fun chordsUpToLevel(level: Level): List<Chord> = ChordsData.chordsUpToLevel(level)
    fun byId(id: String): Chord? = ChordsData.byId(id)
}
