package app.map_m25.data.repository

import app.map_m25.data.local.dao.LocationDao
import app.map_m25.data.local.entity.LocationEntity
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override fun getAllLocations(): Flow<List<MapLocation>> {
        return locationDao.getAllLocations().map { entities ->
            entities.map { it.toMapLocation() }
        }
    }

    override fun getFavoriteLocations(): Flow<List<MapLocation>> {
        return locationDao.getFavoriteLocations().map { entities ->
            entities.map { it.toMapLocation() }
        }
    }

    override fun searchLocations(keyword: String): Flow<List<MapLocation>> {
        return locationDao.searchLocations(keyword).map { entities ->
            entities.map { it.toMapLocation() }
        }
    }

    override suspend fun getLocationById(id: Long): MapLocation? {
        return locationDao.getLocationById(id)?.toMapLocation()
    }

    override suspend fun saveLocation(location: MapLocation): Long {
        return locationDao.insertLocation(LocationEntity.fromMapLocation(location))
    }

    override suspend fun updateLocation(location: MapLocation) {
        locationDao.updateLocation(LocationEntity.fromMapLocation(location))
    }

    override suspend fun deleteLocation(location: MapLocation) {
        locationDao.deleteLocation(LocationEntity.fromMapLocation(location))
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        locationDao.updateFavoriteStatus(id, isFavorite)
    }
}
