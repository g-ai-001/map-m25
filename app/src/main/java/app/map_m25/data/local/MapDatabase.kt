package app.map_m25.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.map_m25.data.local.dao.LocationDao
import app.map_m25.data.local.dao.MarkerCategoryDao
import app.map_m25.data.local.dao.MarkerDao
import app.map_m25.data.local.dao.SearchHistoryDao
import app.map_m25.data.local.dao.TrackDao
import app.map_m25.data.local.entity.LocationEntity
import app.map_m25.data.local.entity.MarkerCategoryEntity
import app.map_m25.data.local.entity.MarkerEntity
import app.map_m25.data.local.entity.OfflineRegionEntity
import app.map_m25.data.local.entity.SearchHistoryEntity
import app.map_m25.data.local.entity.TrackEntity
import app.map_m25.data.local.entity.TrackPointEntity

@Database(
    entities = [
        LocationEntity::class,
        SearchHistoryEntity::class,
        MarkerEntity::class,
        MarkerCategoryEntity::class,
        TrackEntity::class,
        TrackPointEntity::class,
        OfflineRegionEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class MapDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun markerDao(): MarkerDao
    abstract fun markerCategoryDao(): MarkerCategoryDao
    abstract fun trackDao(): TrackDao
    abstract fun offlineRegionDao(): OfflineRegionDao
}
