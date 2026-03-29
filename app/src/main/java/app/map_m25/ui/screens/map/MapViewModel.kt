package app.map_m25.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.data.local.datastore.SettingsDataStore
import app.map_m25.domain.model.MapDisplaySettings
import app.map_m25.domain.model.MapLayer
import app.map_m25.domain.model.MapLocation
import app.map_m25.domain.model.MapMarker
import app.map_m25.domain.model.MapStyle
import app.map_m25.domain.model.RouteType
import app.map_m25.domain.repository.LocationRepository
import app.map_m25.domain.repository.MarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
    val mapStyle: MapStyle = MapStyle.STANDARD,
    val displaySettings: MapDisplaySettings = MapDisplaySettings(),
    val showCompass: Boolean = true,
    val showScaleBar: Boolean = true,
    val isMeasuring: Boolean = false,
    val measurePoints: List<MapLocation> = emptyList(),
    val totalDistance: Float = 0f,
    val markers: List<MapMarker> = emptyList(),
    val isAddingMarker: Boolean = false,
    val voiceEnabled: Boolean = true,
    val navigationState: NavigationState = NavigationState()
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val markerRepository: MarkerRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMarkers()
        loadSettings()
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            markerRepository.getAllMarkers().collect { markers ->
                _uiState.value = _uiState.value.copy(markers = markers)
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.mapStyle.collect { style ->
                _uiState.value = _uiState.value.copy(mapStyle = style)
            }
        }
        viewModelScope.launch {
            settingsDataStore.voiceEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(voiceEnabled = enabled)
            }
        }
        viewModelScope.launch {
            settingsDataStore.showPoiLabels.collect { show ->
                _uiState.value = _uiState.value.copy(
                    displaySettings = _uiState.value.displaySettings.copy(showPoiLabels = show)
                )
            }
        }
        viewModelScope.launch {
            settingsDataStore.showRoadNames.collect { show ->
                _uiState.value = _uiState.value.copy(
                    displaySettings = _uiState.value.displaySettings.copy(showRoadNames = show)
                )
            }
        }
        viewModelScope.launch {
            settingsDataStore.showTrafficSigns.collect { show ->
                _uiState.value = _uiState.value.copy(
                    displaySettings = _uiState.value.displaySettings.copy(showTrafficSigns = show)
                )
            }
        }
        viewModelScope.launch {
            settingsDataStore.showBuildingLabels.collect { show ->
                _uiState.value = _uiState.value.copy(
                    displaySettings = _uiState.value.displaySettings.copy(showBuildingLabels = show)
                )
            }
        }
    }

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
            } else if (_uiState.value.isAddingMarker) {
                val markerName = "标记 ${_uiState.value.markers.size + 1}"
                val marker = MapMarker(
                    name = markerName,
                    latitude = latitude,
                    longitude = longitude,
                    color = 0xFFFF5722.toInt()
                )
                markerRepository.saveMarker(marker)
                _uiState.value = _uiState.value.copy(isAddingMarker = false)
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
        val sinDeltaLatHalf = kotlin.math.sin(deltaLat / 2).toFloat()
        val a = sinDeltaLatHalf * sinDeltaLatHalf +
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
            totalDistance = 0f,
            isAddingMarker = false
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

    fun startAddingMarker() {
        _uiState.value = _uiState.value.copy(
            isAddingMarker = true,
            isMeasuring = false
        )
    }

    fun cancelAddingMarker() {
        _uiState.value = _uiState.value.copy(isAddingMarker = false)
    }

    fun deleteMarker(markerId: Long) {
        viewModelScope.launch {
            markerRepository.deleteMarker(markerId)
        }
    }

    fun startNavigation(routeType: RouteType = RouteType.DRIVING) {
        _uiState.value = _uiState.value.copy(
            navigationState = NavigationState(
                isNavigating = true,
                isSimulation = false,
                isDeviated = false,
                routeType = routeType,
                remainingDistance = "5.2公里",
                remainingTime = "15分钟",
                currentInstruction = "前方直行",
                nextRoadName = "中关村大街"
            )
        )
        checkDeviation()
    }

    fun startSimulationNavigation(routeType: RouteType = RouteType.DRIVING) {
        _uiState.value = _uiState.value.copy(
            navigationState = NavigationState(
                isNavigating = true,
                isSimulation = true,
                isDeviated = false,
                routeType = routeType,
                remainingDistance = "5.2公里",
                remainingTime = "15分钟",
                currentInstruction = "前方直行",
                nextRoadName = "中关村大街"
            )
        )
        startSimulation()
    }

    fun stopNavigation() {
        simulationJob?.cancel()
        simulationJob = null
        _uiState.value = _uiState.value.copy(
            navigationState = NavigationState()
        )
    }

    fun toggleSimulation() {
        val currentNav = _uiState.value.navigationState
        if (currentNav.isSimulation) {
            simulationJob?.cancel()
            simulationJob = null
            _uiState.value = _uiState.value.copy(
                navigationState = currentNav.copy(isSimulation = false)
            )
        } else {
            startSimulationNavigation(currentNav.routeType)
        }
    }

    private var simulationJob: Job? = null

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            var remainingDist = 5.2f
            var remainingTime = 15
            val routeType = _uiState.value.navigationState.routeType
            val instructions = listOf(
                Triple("前方直行", TurnDirection.STRAIGHT, "中关村大街"),
                Triple("前方左转", TurnDirection.LEFT, "科学院南路"),
                Triple("前方右转", TurnDirection.RIGHT, "知春路"),
                Triple("前方直行", TurnDirection.STRAIGHT, "知春路"),
                Triple("前方靠左", TurnDirection.SLIGHT_LEFT, "北四环西路"),
                Triple("前方靠右", TurnDirection.SLIGHT_RIGHT, "中关村一号"),
                Triple("前方掉头", TurnDirection.U_TURN, "中关村一号")
            )
            var stepIndex = 0

            while (remainingDist > 0 && _uiState.value.navigationState.isSimulation) {
                delay(2000)
                remainingDist = (remainingDist - 0.3f).coerceAtLeast(0f)
                remainingTime = (remainingTime - 1).coerceAtLeast(0)

                if (stepIndex < instructions.size && remainingDist < 4.0f - stepIndex * 0.5f) {
                    val (instruction, turn, roadName) = instructions[stepIndex]
                    stepIndex++
                    _uiState.value = _uiState.value.copy(
                        navigationState = _uiState.value.navigationState.copy(
                            currentInstruction = instruction,
                            nextTurnDirection = turn,
                            nextRoadName = roadName
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    navigationState = _uiState.value.navigationState.copy(
                        remainingDistance = formatDistance(remainingDist),
                        remainingTime = "${remainingTime}分钟"
                    )
                )
            }

            if (_uiState.value.navigationState.isSimulation) {
                _uiState.value = _uiState.value.copy(
                    navigationState = _uiState.value.navigationState.copy(
                        currentInstruction = "到达目的地",
                        remainingDistance = "0公里",
                        remainingTime = "0分钟"
                    )
                )
            }
        }
    }

    private fun formatDistance(km: Float): String {
        return if (km >= 1) {
            String.format("%.1f公里", km)
        } else {
            String.format("%.0f米", km * 1000)
        }
    }

    private fun checkDeviation() {
        viewModelScope.launch {
            while (_uiState.value.navigationState.isNavigating) {
                delay(3000)
                val navState = _uiState.value.navigationState
                if (!navState.isNavigating) break

                val deviation = (Math.random() * 100).toInt() % 10
                val isDeviated = deviation < 2

                if (isDeviated != navState.isDeviated) {
                    _uiState.value = _uiState.value.copy(
                        navigationState = navState.copy(
                            isDeviated = isDeviated,
                            currentInstruction = if (isDeviated) "您已偏离路线" else "正在重新规划路线"
                        )
                    )
                }
            }
        }
    }
}
