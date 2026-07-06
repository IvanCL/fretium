package com.fretwise.android.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Profile : Screen("profile")

    data object Dashboard : Screen("dashboard")
    data object Chords : Screen("chords")
    data object Practice : Screen("practice")
    data object Songs : Screen("songs")
    data object Tuner : Screen("tuner")

    data object SongPlayer : Screen("songs/{songId}") {
        fun createRoute(songId: String) = "songs/$songId"
    }
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Inicio", Icons.Filled.Home),
    BottomNavItem(Screen.Chords, "Acordes", Icons.Filled.LibraryMusic),
    BottomNavItem(Screen.Practice, "Práctica", Icons.Filled.MusicNote),
    BottomNavItem(Screen.Songs, "Canciones", Icons.Filled.QueueMusic),
    BottomNavItem(Screen.Tuner, "Afinador", Icons.Filled.Tune),
)
