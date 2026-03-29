package app.map_m25.data.repository

import app.map_m25.data.local.dao.MarkerDao
import app.map_m25.data.local.entity.MarkerEntity
import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.repository.MarkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkerRepositoryImpl @Inject constructor(
    private val markerDao: MarkerDao
) : MarkerRepository {

    override fun getAllMarkers(): Flow<List<MapMarker>> {
        return markerDao.getAllMarkers().map { entities ->
            entities.map { it.toMapMarker() }
        }
    }

    override fun getMarkersByCategory(categoryId: Long): Flow<List<MapMarker>> {
        return markerDao.getMarkersByCategory(categoryId).map { entities ->
            entities.map { it.toMapMarker() }
        }
    }

    override suspend fun getMarkerById(id: Long): MapMarker? {
        return markerDao.getMarkerById(id)?.toMapMarker()
    }

    override suspend fun saveMarker(marker: MapMarker): Long {
        return markerDao.insertMarker(marker.toEntity())
    }

    override suspend fun updateMarker(marker: MapMarker) {
        markerDao.updateMarker(marker.toEntity())
    }

    override suspend fun deleteMarker(id: Long) {
        markerDao.deleteMarkerById(id)
    }

    private fun MarkerEntity.toMapMarker(): MapMarker = MapMarker(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        color = color,
        categoryId = categoryId,
        createdAt = createdAt
    )

    private fun MapMarker.toEntity(): MarkerEntity = MarkerEntity(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        color = color,
        categoryId = categoryId,
        createdAt = createdAt
    )
}
