package app.map_m25.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import app.map_m25.domain.repository.TrackRepository
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

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TracksUiState())
    val uiState: StateFlow<TracksUiState> = _uiState.asStateFlow()

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
            loadTracks()
        }
    }

    private fun calculateTotalDistance(points: List<TrackPoint>): Float {
        if (points.size < 2) return 0f
        var total = 0f
        for (i in 0 until points.size - 1) {
            total += calculateDistance(points[i], points[i + 1])
        }
        return total
    }

    private fun calculateDistance(p1: TrackPoint, p2: TrackPoint): Float {
        val r = 6371f
        val lat1Rad = Math.toRadians(p1.latitude)
        val lat2Rad = Math.toRadians(p2.latitude)
        val deltaLat = Math.toRadians(p2.latitude - p1.latitude)
        val deltaLng = Math.toRadians(p2.longitude - p1.longitude)
        val a = kotlin.math.sin(deltaLat / 2).toFloat() * kotlin.math.sin(deltaLat / 2).toFloat() +
                kotlin.math.cos(lat1Rad).toFloat() * kotlin.math.cos(lat2Rad).toFloat() *
                kotlin.math.sin(deltaLng / 2).toFloat() * kotlin.math.sin(deltaLng / 2).toFloat()
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a.toDouble()).toFloat(), kotlin.math.sqrt((1 - a).toDouble()).toFloat())
        return r * c
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            trackRepository.deleteTrack(trackId)
        }
    }

    fun loadTrackPoints(trackId: Long) {
        viewModelScope.launch {
            val track = trackRepository.getTrackWithPoints(trackId)
            _uiState.value = _uiState.value.copy(
                currentTrack = track,
                currentTrackPoints = track?.points ?: emptyList()
            )
        }
    }
}
