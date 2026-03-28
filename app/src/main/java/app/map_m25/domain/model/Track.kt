package app.map_m25.domain.model

data class Track(
    val id: Long = 0,
    val name: String,
    val points: List<TrackPoint> = emptyList(),
    val totalDistance: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)

data class TrackPoint(
    val id: Long = 0,
    val trackId: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val sequence: Int = 0
)
