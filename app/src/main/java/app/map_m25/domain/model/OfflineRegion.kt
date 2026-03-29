package app.map_m25.domain.model

data class OfflineRegion(
    val id: Long = 0,
    val name: String,
    val minLatitude: Double,
    val maxLatitude: Double,
    val minLongitude: Double,
    val maxLongitude: Double,
    val zoomLevel: Int = 15,
    val size: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun contains(latitude: Double, longitude: Double): Boolean {
        return latitude in minLatitude..maxLatitude && longitude in minLongitude..maxLongitude
    }

    fun centerLatitude(): Double = (minLatitude + maxLatitude) / 2

    fun centerLongitude(): Double = (minLongitude + maxLongitude) / 2
}
