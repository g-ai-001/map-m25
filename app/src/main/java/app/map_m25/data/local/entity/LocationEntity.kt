package app.map_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.map_m25.domain.model.LocationCategory
import app.map_m25.domain.model.MapLocation

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val category: String,
    val isFavorite: Boolean
) {
    fun toMapLocation(): MapLocation = MapLocation(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        address = address,
        category = try {
            LocationCategory.valueOf(category)
        } catch (e: Exception) {
            LocationCategory.OTHER
        },
        isFavorite = isFavorite
    )

    companion object {
        fun fromMapLocation(location: MapLocation): LocationEntity = LocationEntity(
            id = location.id,
            name = location.name,
            latitude = location.latitude,
            longitude = location.longitude,
            address = location.address,
            category = location.category.name,
            isFavorite = location.isFavorite
        )
    }
}
