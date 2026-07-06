package com.fretwise.android.data

import com.fretwise.android.data.model.Level
import com.fretwise.android.data.model.Song
import com.fretwise.android.data.model.SongsData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor() {
    fun all(): List<Song> = SongsData.ALL
    fun songsUpToLevel(level: Level): List<Song> = SongsData.songsUpToLevel(level)
    fun byId(id: String): Song? = SongsData.byId(id)
}
