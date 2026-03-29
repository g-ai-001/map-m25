package app.map_m25.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.map_m25.data.local.datastore.SettingsDataStore
import app.map_m25.domain.model.MapDisplaySettings
import app.map_m25.domain.model.MapLayer
import app.map_m25.domain.model.MapStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean = false,
    val mapZoom: Float = 15f,
    val keepScreenOn: Boolean = false,
    val highRefreshRate: Boolean = true,
    val mapLayer: MapLayer = MapLayer.NORMAL,
    val mapStyle: MapStyle = MapStyle.STANDARD,
    val voiceEnabled: Boolean = true,
    val displaySettings: MapDisplaySettings = MapDisplaySettings()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsDataStore.darkMode,
                settingsDataStore.mapZoom,
                settingsDataStore.keepScreenOn,
                settingsDataStore.highRefreshRate,
                settingsDataStore.mapLayer,
                settingsDataStore.mapStyle
            ) { darkMode, mapZoom, keepScreenOn, highRefreshRate, mapLayer, mapStyle ->
                DarkModeData(darkMode, mapZoom, keepScreenOn, highRefreshRate, mapLayer, mapStyle)
            }.collect { basic ->
                combine(
                    settingsDataStore.voiceEnabled,
                    settingsDataStore.showPoiLabels,
                    settingsDataStore.showRoadNames,
                    settingsDataStore.showTrafficSigns,
                    settingsDataStore.showBuildingLabels
                ) { voiceEnabled, showPoi, showRoad, showTraffic, showBuilding ->
                    DisplaySettingsData(voiceEnabled, showPoi, showRoad, showTraffic, showBuilding)
                }.collect { display ->
                    _uiState.value = SettingsUiState(
                        darkMode = basic.darkMode,
                        mapZoom = basic.mapZoom,
                        keepScreenOn = basic.keepScreenOn,
                        highRefreshRate = basic.highRefreshRate,
                        mapLayer = basic.mapLayer,
                        mapStyle = basic.mapStyle,
                        voiceEnabled = display.voiceEnabled,
                        displaySettings = MapDisplaySettings(
                            showPoiLabels = display.showPoi,
                            showRoadNames = display.showRoad,
                            showTrafficSigns = display.showTraffic,
                            showBuildingLabels = display.showBuilding
                        )
                    )
                }
            }
        }
    }

    private data class DarkModeData(
        val darkMode: Boolean,
        val mapZoom: Float,
        val keepScreenOn: Boolean,
        val highRefreshRate: Boolean,
        val mapLayer: MapLayer,
        val mapStyle: MapStyle
    )

    private data class DisplaySettingsData(
        val voiceEnabled: Boolean,
        val showPoi: Boolean,
        val showRoad: Boolean,
        val showTraffic: Boolean,
        val showBuilding: Boolean
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
        }
    }

    fun setMapZoom(zoom: Float) {
        viewModelScope.launch {
            settingsDataStore.setMapZoom(zoom)
        }
    }

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setKeepScreenOn(enabled)
        }
    }

    fun setHighRefreshRate(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setHighRefreshRate(enabled)
        }
    }

    fun setMapLayer(layer: MapLayer) {
        viewModelScope.launch {
            settingsDataStore.setMapLayer(layer)
        }
    }

    fun setMapStyle(style: MapStyle) {
        viewModelScope.launch {
            settingsDataStore.setMapStyle(style)
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setVoiceEnabled(enabled)
        }
    }

    fun setShowPoiLabels(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowPoiLabels(show)
        }
    }

    fun setShowRoadNames(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowRoadNames(show)
        }
    }

    fun setShowTrafficSigns(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowTrafficSigns(show)
        }
    }

    fun setShowBuildingLabels(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowBuildingLabels(show)
        }
    }
}
