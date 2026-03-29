package app.map_m25.ui.screens.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.model.Route
import app.map_m25.domain.model.RouteType
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.util.DistanceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouteUiState(
    val startLocation: MapLocation? = null,
    val endLocation: MapLocation? = null,
    val routeType: RouteType = RouteType.DRIVING,
    val route: Route? = null,
    val isNavigating: Boolean = false,
    val allLocations: List<MapLocation> = emptyList()
)

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            locationRepository.getAllLocations().collect { locations ->
                _uiState.value = _uiState.value.copy(allLocations = locations)
            }
        }
    }

    fun setStartLocation(location: MapLocation) {
        _uiState.value = _uiState.value.copy(startLocation = location)
        calculateRoute()
    }

    fun setEndLocation(location: MapLocation) {
        _uiState.value = _uiState.value.copy(endLocation = location)
        calculateRoute()
    }

    fun setRouteType(type: RouteType) {
        _uiState.value = _uiState.value.copy(routeType = type)
        calculateRoute()
    }

    fun swapLocations() {
        val current = _uiState.value
        _uiState.value = current.copy(
            startLocation = current.endLocation,
            endLocation = current.startLocation
        )
        calculateRoute()
    }

    private fun calculateRoute() {
        val start = _uiState.value.startLocation
        val end = _uiState.value.endLocation

        if (start != null && end != null) {
            val distance = DistanceUtils.calculateDistance(
                start.latitude, start.longitude,
                end.latitude, end.longitude
            )
            val duration = calculateDuration(distance, _uiState.value.routeType)

            val route = Route(
                startLocation = start,
                endLocation = end,
                distance = distance,
                duration = duration,
                routeType = _uiState.value.routeType
            )
            _uiState.value = _uiState.value.copy(route = route)
        }
    }

    private fun calculateDuration(distanceKm: Float, routeType: RouteType): Int {
        val speedKmh = when (routeType) {
            RouteType.DRIVING -> 40.0
            RouteType.WALKING -> 5.0
            RouteType.CYCLING -> 15.0
        }
        return ((distanceKm / speedKmh) * 60).toInt()
    }

    fun startNavigation() {
        _uiState.value = _uiState.value.copy(isNavigating = true)
    }

    fun stopNavigation() {
        _uiState.value = _uiState.value.copy(isNavigating = false)
    }

    fun clearRoute() {
        _uiState.value = _uiState.value.copy(
            startLocation = null,
            endLocation = null,
            route = null
        )
    }
}
