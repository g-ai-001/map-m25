package app.map_m25.domain.model

data class MapLocation(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val category: LocationCategory = LocationCategory.OTHER,
    val isFavorite: Boolean = false
)

enum class LocationCategory {
    HOME,
    WORK,
    FOOD,
    SHOPPING,
    ENTERTAINMENT,
    TRANSPORT,
    OTHER
}

enum class MapLayer {
    NORMAL,
    SATELLITE,
    TERRAIN
}
