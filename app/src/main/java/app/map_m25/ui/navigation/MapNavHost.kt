package app.map_m25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.map_m25.ui.screens.favorites.FavoritesScreen
import app.map_m25.ui.screens.history.HistoryScreen
import app.map_m25.ui.screens.hotspots.HotSpotsScreen
import app.map_m25.ui.screens.map.MapScreen
import app.map_m25.ui.screens.map.MapViewModel
import app.map_m25.ui.screens.markers.MarkersScreen
import app.map_m25.ui.screens.offline.OfflineRegionsScreen
import app.map_m25.ui.screens.route.RouteScreen
import app.map_m25.ui.screens.search.SearchScreen
import app.map_m25.ui.screens.settings.SettingsScreen
import app.map_m25.ui.screens.snapshot.MapSnapshotsScreen
import app.map_m25.ui.screens.export.ExportScreen
import app.map_m25.ui.screens.tracks.TrackPlaybackScreen
import app.map_m25.ui.screens.tracks.TrackStatsScreen
import app.map_m25.ui.screens.tracks.TracksScreen

@Composable
fun MapNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        composable(Screen.Map.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Map.route)
            }
            val mapViewModel: MapViewModel = androidx.hilt.navigation.compose.hiltViewModel(parentEntry)
            MapScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToRoute = { navController.navigate(Screen.Route.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToMarkers = { navController.navigate(Screen.Markers.route) },
                onNavigateToTracks = { navController.navigate(Screen.Tracks.route) },
                viewModel = mapViewModel
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onLocationSelected = { _ -> navController.popBackStack() }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Route.route) {
            val parentEntry = remember(Unit) { navController.getBackStackEntry(Screen.Map.route) }
            val mapViewModel: MapViewModel = hiltViewModel(parentEntry)
            RouteScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartNavigation = { routeType ->
                    mapViewModel.startNavigation(routeType)
                    navController.popBackStack()
                },
                onStartSimulation = { routeType ->
                    mapViewModel.startSimulationNavigation(routeType)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToExport = { navController.navigate(Screen.Export.route) },
                onNavigateToOfflineRegions = { navController.navigate(Screen.OfflineRegions.route) },
                onNavigateToHotSpots = { navController.navigate(Screen.HotSpots.route) },
                onNavigateToSnapshots = { navController.navigate(Screen.MapSnapshots.route) }
            )
        }
        composable(Screen.Markers.route) {
            MarkersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Tracks.route) {
            TracksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStats = { trackId ->
                    navController.navigate(Screen.TrackStats.createRoute(trackId))
                }
            )
        }
        composable(
            route = Screen.TrackStats.route,
            arguments = listOf(navArgument("trackId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L
            TrackStatsScreen(
                trackId = trackId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayback = { id ->
                    navController.navigate(Screen.TrackPlayback.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.TrackPlayback.route,
            arguments = listOf(navArgument("trackId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L
            TrackPlaybackScreen(
                trackId = trackId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Export.route) {
            ExportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.OfflineRegions.route) {
            OfflineRegionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.HotSpots.route) {
            HotSpotsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLocationClick = { _ -> navController.popBackStack() }
            )
        }
        composable(Screen.MapSnapshots.route) {
            MapSnapshotsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
