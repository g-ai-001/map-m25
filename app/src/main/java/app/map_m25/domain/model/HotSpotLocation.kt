package app.map_m25.domain.model

data class HotSpotLocation(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val visitCount: Int,
    val category: LocationCategory
)
