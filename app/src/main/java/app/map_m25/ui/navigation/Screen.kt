package app.map_m25.ui.navigation

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object History : Screen("history")
    data object Route : Screen("route")
    data object Settings : Screen("settings")
    data object Markers : Screen("markers")
    data object Tracks : Screen("tracks")
    data object TrackStats : Screen("track_stats/{trackId}") {
        fun createRoute(trackId: Long) = "track_stats/$trackId"
    }
    data object TrackPlayback : Screen("track_playback/{trackId}") {
        fun createRoute(trackId: Long) = "track_playback/$trackId"
    }
    data object Export : Screen("export")
    data object OfflineRegions : Screen("offline_regions")
    data object HotSpots : Screen("hot_spots")
    data object MapSnapshots : Screen("map_snapshots")
}
