package app.map_m25.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val showLocationInfo: Boolean = false
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
            _uiState.value = _uiState.value.copy(
                selectedLocation = location,
                showLocationInfo = true
            )
        }
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
}
