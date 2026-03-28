package app.map_m25.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapLayer
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val currentLocation: MapLocation = MapLocation(
        name = "我的位置",
        latitude = 39.9042,
        longitude = 116.4074,
        address = "北京市"
    ),
    val zoom: Float = 15f,
    val rotation: Float = 0f,
    val isLocating: Boolean = false,
    val selectedLocation: MapLocation? = null,
    val showLocationInfo: Boolean = false,
    val mapLayer: MapLayer = MapLayer.NORMAL,
    val showCompass: Boolean = true,
    val showScaleBar: Boolean = true,
    val isMeasuring: Boolean = false,
    val measurePoints: List<MapLocation> = emptyList(),
    val totalDistance: Float = 0f
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onMapClick(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val location = MapLocation(
                name = "选中位置",
                latitude = latitude,
                longitude = longitude,
                address = "经度: ${String.format("%.6f", longitude)}, 纬度: ${String.format("%.6f", latitude)}"
            )
            if (_uiState.value.isMeasuring) {
                val newPoints = _uiState.value.measurePoints + location
                val newDistance = calculateTotalDistance(newPoints)
                _uiState.value = _uiState.value.copy(
                    measurePoints = newPoints,
                    totalDistance = newDistance
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    selectedLocation = location,
                    showLocationInfo = true
                )
            }
        }
    }

    private fun calculateTotalDistance(points: List<MapLocation>): Float {
        if (points.size < 2) return 0f
        var total = 0f
        for (i in 0 until points.size - 1) {
            total += calculateDistance(points[i], points[i + 1])
        }
        return total
    }

    private fun calculateDistance(p1: MapLocation, p2: MapLocation): Float {
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

    fun onZoomChange(zoom: Float) {
        _uiState.value = _uiState.value.copy(zoom = zoom.coerceIn(5f, 20f))
    }

    fun onRotationChange(rotation: Float) {
        _uiState.value = _uiState.value.copy(rotation = rotation % 360f)
    }

    fun resetRotation() {
        _uiState.value = _uiState.value.copy(rotation = 0f)
    }

    fun startLocation() {
        _uiState.value = _uiState.value.copy(isLocating = true)
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isLocating = false)
        }
    }

    fun hideLocationInfo() {
        _uiState.value = _uiState.value.copy(
            showLocationInfo = false,
            selectedLocation = null
        )
    }

    fun addToFavorites(location: MapLocation) {
        viewModelScope.launch {
            val favoriteLocation = location.copy(isFavorite = true)
            locationRepository.saveLocation(favoriteLocation)
        }
    }

    fun setMapLayer(layer: MapLayer) {
        _uiState.value = _uiState.value.copy(mapLayer = layer)
    }

    fun toggleCompass() {
        _uiState.value = _uiState.value.copy(showCompass = !_uiState.value.showCompass)
    }

    fun toggleScaleBar() {
        _uiState.value = _uiState.value.copy(showScaleBar = !_uiState.value.showScaleBar)
    }

    fun startMeasuring() {
        _uiState.value = _uiState.value.copy(
            isMeasuring = true,
            measurePoints = emptyList(),
            totalDistance = 0f
        )
    }

    fun stopMeasuring() {
        _uiState.value = _uiState.value.copy(
            isMeasuring = false,
            measurePoints = emptyList(),
            totalDistance = 0f
        )
    }

    fun clearMeasurePoints() {
        _uiState.value = _uiState.value.copy(
            measurePoints = emptyList(),
            totalDistance = 0f
        )
    }
}
