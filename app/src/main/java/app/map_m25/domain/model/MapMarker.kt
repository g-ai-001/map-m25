package app.map_m25.domain.model

data class MapMarker(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis()
)
