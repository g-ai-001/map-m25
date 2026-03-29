package app.map_m25.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.Track
import app.map_m25.domain.model.TrackPoint
import app.map_m25.domain.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackPlaybackState(
    val track: Track? = null,
    val points: List<TrackPoint> = emptyList(),
    val isPlaying: Boolean = false,
    val currentPointIndex: Int = 0,
    val currentPoint: TrackPoint? = null,
    val progress: Float = 0f,
    val playbackSpeed: Float = 1f,
    val elapsedTime: Long = 0L,
    val totalDuration: Long = 0L
)

@HiltViewModel
class TrackPlaybackViewModel @Inject constructor(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _playbackState = MutableStateFlow(TrackPlaybackState())
    val playbackState: StateFlow<TrackPlaybackState> = _playbackState.asStateFlow()

    private var playbackJob: Job? = null

    fun loadTrack(trackId: Long) {
        viewModelScope.launch {
            val track = trackRepository.getTrackWithPoints(trackId)
            track?.let {
                val duration = if (track.points.size > 1) {
                    track.points.last().timestamp - track.points.first().timestamp
                } else 0L
                _playbackState.value = TrackPlaybackState(
                    track = track,
                    points = track.points,
                    totalDuration = duration
                )
            }
        }
    }

    fun play() {
        if (_playbackState.value.points.isEmpty()) return

        _playbackState.value = _playbackState.value.copy(isPlaying = true)
        startPlayback()
    }

    fun pause() {
        playbackJob?.cancel()
        playbackJob = null
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentPointIndex = 0,
            currentPoint = null,
            progress = 0f,
            elapsedTime = 0L
        )
    }

    fun setSpeed(speed: Float) {
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
        if (_playbackState.value.isPlaying) {
            playbackJob?.cancel()
            startPlayback()
        }
    }

    fun seekTo(progress: Float) {
        val points = _playbackState.value.points
        if (points.isEmpty()) return

        val index = (progress * (points.size - 1)).toInt().coerceIn(0, points.size - 1)
        val point = points[index]
        val elapsed = point.timestamp - points.first().timestamp

        _playbackState.value = _playbackState.value.copy(
            currentPointIndex = index,
            currentPoint = point,
            progress = progress,
            elapsedTime = elapsed
        )
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val points = _playbackState.value.points
            val speed = _playbackState.value.playbackSpeed
            var currentIndex = _playbackState.value.currentPointIndex

            while (currentIndex < points.size - 1 && _playbackState.value.isPlaying) {
                val currentPoint = points[currentIndex]
                val nextPoint = points[currentIndex + 1]
                val segmentDuration = (nextPoint.timestamp - currentPoint.timestamp) / speed

                _playbackState.value = _playbackState.value.copy(
                    currentPointIndex = currentIndex,
                    currentPoint = currentPoint,
                    progress = currentIndex.toFloat() / (points.size - 1).toFloat(),
                    elapsedTime = currentPoint.timestamp - points.first().timestamp
                )

                delay(segmentDuration.toLong().coerceAtLeast(50L))
                currentIndex++
            }

            if (currentIndex >= points.size - 1) {
                _playbackState.value = _playbackState.value.copy(
                    isPlaying = false,
                    progress = 1f,
                    currentPointIndex = points.size - 1,
                    currentPoint = points.lastOrNull(),
                    elapsedTime = _playbackState.value.totalDuration
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}