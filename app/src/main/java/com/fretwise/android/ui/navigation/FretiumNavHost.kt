package com.fretwise.android.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fretwise.android.ui.auth.ProfileScreen
import com.fretwise.android.ui.auth.SessionState
import com.fretwise.android.ui.auth.SessionViewModel
import com.fretwise.android.ui.chords.ChordsScreen
import com.fretwise.android.ui.dashboard.DashboardScreen
import com.fretwise.android.ui.practice.PracticeScreen
import com.fretwise.android.ui.songs.SongPlayerScreen
import com.fretwise.android.ui.songs.SongsScreen
import com.fretwise.android.ui.tuner.TunerScreen

@Composable
fun FretiumApp() {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val sessionState by sessionViewModel.state.collectAsStateWithLifecycle()

    when (sessionState) {
        SessionState.Loading -> SplashScreen()
        else -> AuthGatedNavHost(
            isLoggedIn = sessionState is SessionState.LoggedIn,
            onLogout = sessionViewModel::logout,
        )
    }
}

@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthGatedNavHost(isLoggedIn: Boolean, onLogout: () -> Unit) {
    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        val target = if (isLoggedIn) Screen.Dashboard.route else Screen.Profile.route
        navController.navigate(target) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) FretiumBottomBar(currentRoute = currentRoute) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Profile.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onLogout = onLogout,
                    onNavigateToChords = { navController.navigate(Screen.Chords.route) },
                    onNavigateToPractice = { navController.navigate(Screen.Practice.route) },
                    onNavigateToSongs = { navController.navigate(Screen.Songs.route) },
                    onNavigateToTuner = { navController.navigate(Screen.Tuner.route) },
                )
            }
            composable(Screen.Chords.route) { ChordsScreen() }
            composable(Screen.Practice.route) { PracticeScreen() }
            composable(Screen.Songs.route) {
                SongsScreen(onSongClick = { songId -> navController.navigate(Screen.SongPlayer.createRoute(songId)) })
            }
            composable(
                route = Screen.SongPlayer.route,
                arguments = listOf(navArgument("songId") { type = NavType.StringType }),
            ) {
                SongPlayerScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Tuner.route) { TunerScreen() }
        }
    }
}

@Composable
private fun FretiumBottomBar(currentRoute: String?, onNavigate: (Screen) -> Unit) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = { onNavigate(item.screen) },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}
