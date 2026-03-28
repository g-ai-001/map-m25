package app.map_m25.domain.repository

import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    suspend fun getTrackById(id: Long): Track?
    suspend fun getTrackWithPoints(id: Long): Track?
    fun getTrackPointsFlow(trackId: Long): Flow<List<TrackPoint>>
    suspend fun saveTrack(track: Track): Long
    suspend fun addTrackPoint(trackId: Long, point: TrackPoint)
    suspend fun updateTrack(track: Track)
    suspend fun deleteTrack(id: Long)
    suspend fun clearTrackPoints(trackId: Long)
}
