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

enum class MapStyle(val displayName: String) {
    STANDARD("标准"),
    DARK("深色"),
    LIGHT("浅色"),
    NAVY("海军蓝"),
    GREEN("绿色")
}

data class MapDisplaySettings(
    val showPoiLabels: Boolean = true,
    val showRoadNames: Boolean = true,
    val showTrafficSigns: Boolean = true,
    val showBuildingLabels: Boolean = true
)
