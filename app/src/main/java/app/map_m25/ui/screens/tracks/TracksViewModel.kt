package app.map_m25.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import app.map_m25.domain.repository.TrackRepository
import app.map_m25.util.DistanceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TracksUiState(
    val tracks: List<Track> = emptyList(),
    val currentTrack: Track? = null,
    val currentTrackPoints: List<TrackPoint> = emptyList(),
    val isLoading: Boolean = false,
    val isRecording: Boolean = false,
    val currentLocation: TrackPoint? = null
)

data class TrackStatistics(
    val totalDistance: Float = 0f,
    val totalDuration: Long = 0L,
    val avgSpeed: Float = 0f,
    val maxSpeed: Float = 0f,
    val elevationGain: Float = 0f,
    val elevationLoss: Float = 0f
)

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TracksUiState())
    val uiState: StateFlow<TracksUiState> = _uiState.asStateFlow()

    private val _statistics = MutableStateFlow(TrackStatistics())
    val statistics: StateFlow<TrackStatistics> = _statistics.asStateFlow()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            trackRepository.getAllTracks().collect { tracks ->
                _uiState.value = _uiState.value.copy(
                    tracks = tracks,
                    isLoading = false
                )
            }
        }
    }

    fun startRecording(name: String) {
        viewModelScope.launch {
            val trackId = trackRepository.saveTrack(Track(name = name))
            _uiState.value = _uiState.value.copy(
                isRecording = true,
                currentTrack = Track(id = trackId, name = name)
            )
            _statistics.value = TrackStatistics()
        }
    }

    fun addTrackPoint(latitude: Double, longitude: Double) {
        if (!_uiState.value.isRecording) return
        viewModelScope.launch {
            val currentTrack = _uiState.value.currentTrack ?: return@launch
            val point = TrackPoint(
                trackId = currentTrack.id,
                latitude = latitude,
                longitude = longitude,
                sequence = _uiState.value.currentTrackPoints.size
            )
            trackRepository.addTrackPoint(currentTrack.id, point)
            val newPoints = _uiState.value.currentTrackPoints + point
            updateStatistics(newPoints)
            _uiState.value = _uiState.value.copy(
                currentTrackPoints = newPoints,
                currentLocation = point
            )
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            val currentTrack = _uiState.value.currentTrack ?: return@launch
            val totalDistance = calculateTotalDistance(_uiState.value.currentTrackPoints)
            trackRepository.updateTrack(currentTrack.copy(totalDistance = totalDistance))
            _uiState.value = _uiState.value.copy(
                isRecording = false,
                currentTrack = null,
                currentTrackPoints = emptyList()
            )
            _statistics.value = TrackStatistics()
            loadTracks()
        }
    }

    fun loadTrackStatistics(trackId: Long) {
        viewModelScope.launch {
            val track = trackRepository.getTrackWithPoints(trackId)
            track?.let {
                _uiState.value = _uiState.value.copy(
                    currentTrack = track,
                    currentTrackPoints = track.points
                )
                updateStatistics(track.points)
            }
        }
    }

    private fun updateStatistics(points: List<TrackPoint>) {
        if (points.size < 2) {
            _statistics.value = TrackStatistics()
            return
        }
        val totalDistance = calculateTotalDistance(points)
        val duration = points.last().timestamp - points.first().timestamp
        val avgSpeed = if (duration > 0) (totalDistance / (duration / 3600000f)) else 0f
        val maxSpeed = calculateMaxSpeed(points)

        _statistics.value = TrackStatistics(
            totalDistance = totalDistance,
            totalDuration = duration,
            avgSpeed = avgSpeed,
            maxSpeed = maxSpeed,
            elevationGain = 0f,
            elevationLoss = 0f
        )
    }

    private fun calculateTotalDistance(points: List<TrackPoint>): Float {
        if (points.size < 2) return 0f
        var total = 0f
        for (i in 0 until points.size - 1) {
            total += DistanceUtils.calculateDistance(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude
            )
        }
        return total
    }

    private fun calculateMaxSpeed(points: List<TrackPoint>): Float {
        var maxSpeed = 0f
        for (i in 1 until points.size) {
            val dist = DistanceUtils.calculateDistance(
                points[i - 1].latitude, points[i - 1].longitude,
                points[i].latitude, points[i].longitude
            )
            val time = (points[i].timestamp - points[i - 1].timestamp) / 3600000f
            if (time > 0) {
                val speed = dist / time
                if (speed > maxSpeed) maxSpeed = speed
            }
        }
        return maxSpeed
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            trackRepository.deleteTrack(trackId)
        }
    }
}
