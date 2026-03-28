package app.map_m25.data.repository

import app.map_m25.data.local.dao.TrackDao
import app.map_m25.data.local.entity.TrackEntity
import app.map_m25.data.local.entity.TrackPointEntity
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import app.map_m25.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<Track>> {
        return trackDao.getAllTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun getTrackById(id: Long): Track? {
        return trackDao.getTrackById(id)?.toTrack()
    }

    override suspend fun getTrackWithPoints(id: Long): Track? {
        val track = trackDao.getTrackById(id)?.toTrack() ?: return null
        val points = trackDao.getTrackPoints(id).map { it.toTrackPoint() }
        return track.copy(points = points)
    }

    override fun getTrackPointsFlow(trackId: Long): Flow<List<TrackPoint>> {
        return trackDao.getTrackPointsFlow(trackId).map { entities ->
            entities.map { it.toTrackPoint() }
        }
    }

    override suspend fun saveTrack(track: Track): Long {
        return trackDao.insertTrack(track.toEntity())
    }

    override suspend fun addTrackPoint(trackId: Long, point: TrackPoint) {
        trackDao.insertTrackPoint(point.toEntity(trackId))
    }

    override suspend fun updateTrack(track: Track) {
        trackDao.updateTrack(track.toEntity())
    }

    override suspend fun deleteTrack(id: Long) {
        trackDao.deleteTrackById(id)
    }

    override suspend fun clearTrackPoints(trackId: Long) {
        trackDao.deleteTrackPoints(trackId)
    }

    private fun TrackEntity.toTrack(): Track = Track(
        id = id,
        name = name,
        totalDistance = totalDistance,
        createdAt = createdAt
    )

    private fun Track.toEntity(): TrackEntity = TrackEntity(
        id = id,
        name = name,
        totalDistance = totalDistance,
        createdAt = createdAt
    )

    private fun TrackPointEntity.toTrackPoint(): TrackPoint = TrackPoint(
        id = id,
        trackId = trackId,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        sequence = sequence
    )

    private fun TrackPoint.toEntity(trackId: Long): TrackPointEntity = TrackPointEntity(
        id = id,
        trackId = trackId,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        sequence = sequence
    )
}
