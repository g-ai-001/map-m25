package app.map_m25.domain.repository

import app.map_m25.domain.model.MapMarker
import kotlinx.coroutines.flow.Flow

interface MarkerRepository {
    fun getAllMarkers(): Flow<List<MapMarker>>
    fun getMarkersByCategory(categoryId: Long): Flow<List<MapMarker>>
    suspend fun getMarkerById(id: Long): MapMarker?
    suspend fun saveMarker(marker: MapMarker): Long
    suspend fun updateMarker(marker: MapMarker)
    suspend fun deleteMarker(id: Long)
}
