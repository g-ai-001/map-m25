package app.map_m25.domain.model

data class MarkerCategory(
    val id: Long = 0,
    val name: String,
    val color: Int,
    val iconName: String = "LocationOn",
    val createdAt: Long = System.currentTimeMillis()
)