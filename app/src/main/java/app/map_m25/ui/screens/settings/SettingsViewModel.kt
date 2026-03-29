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
                settingsDataStore.mapStyle,
                settingsDataStore.voiceEnabled,
                settingsDataStore.showPoiLabels,
                settingsDataStore.showRoadNames,
                settingsDataStore.showTrafficSigns,
                settingsDataStore.showBuildingLabels
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                SettingsUiState(
                    darkMode = values[0] as Boolean,
                    mapZoom = values[1] as Float,
                    keepScreenOn = values[2] as Boolean,
                    highRefreshRate = values[3] as Boolean,
                    mapLayer = values[4] as MapLayer,
                    mapStyle = values[5] as MapStyle,
                    voiceEnabled = values[6] as Boolean,
                    displaySettings = MapDisplaySettings(
                        showPoiLabels = values[7] as Boolean,
                        showRoadNames = values[8] as Boolean,
                        showTrafficSigns = values[9] as Boolean,
                        showBuildingLabels = values[10] as Boolean
                    )
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

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
