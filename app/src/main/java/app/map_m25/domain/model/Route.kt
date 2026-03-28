package app.map_m25.domain.model

data class Route(
    val id: Long = 0,
    val startLocation: MapLocation,
    val endLocation: MapLocation,
    val waypoints: List<MapLocation> = emptyList(),
    val distance: Float = 0f,
    val duration: Int = 0,
    val routeType: RouteType = RouteType.DRIVING
)

enum class RouteType {
    DRIVING,
    WALKING,
    CYCLING
}
