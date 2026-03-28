package app.map_m25.ui.navigation

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Route : Screen("route")
    data object Settings : Screen("settings")
}
