package app.map_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import app.map_m25.data.local.entity.TrackEntity
import app.map_m25.data.local.entity.TrackPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY createdAt DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): TrackEntity?

    @Query("SELECT * FROM track_points WHERE trackId = :trackId ORDER BY sequence ASC")
    suspend fun getTrackPoints(trackId: Long): List<TrackPointEntity>

    @Query("SELECT * FROM track_points WHERE trackId = :trackId ORDER BY sequence ASC")
    fun getTrackPointsFlow(trackId: Long): Flow<List<TrackPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackPoint(point: TrackPointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackPoints(points: List<TrackPointEntity>)

    @Update
    suspend fun updateTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrackById(id: Long)

    @Query("DELETE FROM track_points WHERE trackId = :trackId")
    suspend fun deleteTrackPoints(trackId: Long)
}
