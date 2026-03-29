package app.map_m25.di

import android.content.Context
import androidx.room.Room
import app.map_m25.data.local.MapDatabase
import app.map_m25.data.local.dao.LocationDao
import app.map_m25.data.local.dao.MarkerCategoryDao
import app.map_m25.data.local.dao.MarkerDao
import app.map_m25.data.local.dao.SearchHistoryDao
import app.map_m25.data.local.dao.TrackDao
import app.map_m25.data.local.datastore.SettingsDataStore
import app.map_m25.data.repository.LocationRepositoryImpl
import app.map_m25.data.repository.MarkerCategoryRepositoryImpl
import app.map_m25.data.repository.MarkerRepositoryImpl
import app.map_m25.data.repository.SearchHistoryRepositoryImpl
import app.map_m25.data.repository.TrackRepositoryImpl
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.domain.repository.MarkerCategoryRepository
import app.map_m25.domain.repository.MarkerRepository
import app.map_m25.domain.repository.SearchHistoryRepository
import app.map_m25.domain.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MapDatabase {
        return Room.databaseBuilder(
            context,
            MapDatabase::class.java,
            "map_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: MapDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: MapDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideMarkerDao(database: MapDatabase): MarkerDao {
        return database.markerDao()
    }

    @Provides
    @Singleton
    fun provideMarkerCategoryDao(database: MapDatabase): MarkerCategoryDao {
        return database.markerCategoryDao()
    }

    @Provides
    @Singleton
    fun provideTrackDao(database: MapDatabase): TrackDao {
        return database.trackDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(locationDao: LocationDao): LocationRepository {
        return LocationRepositoryImpl(locationDao)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(searchHistoryDao: SearchHistoryDao): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(searchHistoryDao)
    }

    @Provides
    @Singleton
    fun provideMarkerRepository(markerDao: MarkerDao): MarkerRepository {
        return MarkerRepositoryImpl(markerDao)
    }

    @Provides
    @Singleton
    fun provideMarkerCategoryRepository(markerCategoryDao: MarkerCategoryDao): MarkerCategoryRepository {
        return MarkerCategoryRepositoryImpl(markerCategoryDao)
    }

    @Provides
    @Singleton
    fun provideTrackRepository(trackDao: TrackDao): TrackRepository {
        return TrackRepositoryImpl(trackDao)
    }
}
