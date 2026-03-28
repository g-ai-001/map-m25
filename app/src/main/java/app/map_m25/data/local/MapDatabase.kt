package app.map_m25.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.map_m25.data.local.dao.LocationDao
import app.map_m25.data.local.dao.SearchHistoryDao
import app.map_m25.data.local.entity.LocationEntity
import app.map_m25.data.local.entity.SearchHistoryEntity

@Database(
    entities = [LocationEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MapDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
