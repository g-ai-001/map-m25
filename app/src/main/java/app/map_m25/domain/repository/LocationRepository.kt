package app.map_m25.domain.repository

import app.map_m25.domain.model.MapLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<MapLocation>>
    fun getFavoriteLocations(): Flow<List<MapLocation>>
    fun searchLocations(keyword: String): Flow<List<MapLocation>>
    suspend fun getLocationById(id: Long): MapLocation?
    suspend fun saveLocation(location: MapLocation): Long
    suspend fun updateLocation(location: MapLocation)
    suspend fun deleteLocation(location: MapLocation)
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
