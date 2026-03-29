package app.map_m25.domain.repository

import app.map_m25.domain.model.OfflineRegion
import kotlinx.coroutines.flow.Flow

interface OfflineRegionRepository {
    fun getAllRegions(): Flow<List<OfflineRegion>>
    suspend fun getRegionById(id: Long): OfflineRegion?
    suspend fun insertRegion(region: OfflineRegion): Long
    suspend fun updateRegion(region: OfflineRegion)
    suspend fun deleteRegion(id: Long)
}
