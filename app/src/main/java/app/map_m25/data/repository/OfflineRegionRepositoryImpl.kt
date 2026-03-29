package app.map_m25.data.repository

import app.map_m25.data.local.dao.OfflineRegionDao
import app.map_m25.data.local.entity.OfflineRegionEntity
import app.map_m25.domain.model.OfflineRegion
import app.map_m25.domain.repository.OfflineRegionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRegionRepositoryImpl @Inject constructor(
    private val offlineRegionDao: OfflineRegionDao
) : OfflineRegionRepository {

    override fun getAllRegions(): Flow<List<OfflineRegion>> {
        return offlineRegionDao.getAllRegions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRegionById(id: Long): OfflineRegion? {
        return offlineRegionDao.getRegionById(id)?.toDomain()
    }

    override suspend fun insertRegion(region: OfflineRegion): Long {
        return offlineRegionDao.insertRegion(region.toEntity())
    }

    override suspend fun updateRegion(region: OfflineRegion) {
        offlineRegionDao.updateRegion(region.toEntity())
    }

    override suspend fun deleteRegion(id: Long) {
        offlineRegionDao.deleteRegionById(id)
    }

    private fun OfflineRegionEntity.toDomain(): OfflineRegion {
        return OfflineRegion(
            id = id,
            name = name,
            minLatitude = minLatitude,
            maxLatitude = maxLatitude,
            minLongitude = minLongitude,
            maxLongitude = maxLongitude,
            zoomLevel = zoomLevel,
            size = size,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun OfflineRegion.toEntity(): OfflineRegionEntity {
        return OfflineRegionEntity(
            id = id,
            name = name,
            minLatitude = minLatitude,
            maxLatitude = maxLatitude,
            minLongitude = minLongitude,
            maxLongitude = maxLongitude,
            zoomLevel = zoomLevel,
            size = size,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
